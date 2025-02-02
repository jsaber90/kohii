/*
 * Copyright (c) 2020 Nam Nguyen, nam@ene.im
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package kohii.v1.exoplayer

import android.content.Context
import com.google.android.exoplayer2.ui.PlayerView
import kohii.v1.core.BridgeCreator
import kohii.v1.core.Common
import kohii.v1.core.Master
import kohii.v1.core.Playable
import kohii.v1.core.Playable.Config
import kohii.v1.core.PlayableCreator
import kohii.v1.media.Media
import kotlin.LazyThreadSafetyMode.NONE

@Deprecated(
  message = "PlayerView is deprecated. Use the StyledPlayerViewBridgeCreatorFactory instead."
)
typealias PlayerViewBridgeCreatorFactory = (Context) -> BridgeCreator<PlayerView>

@Deprecated(message = "PlayerView is deprecated. Use the StyledPlayerViewPlayableCreator instead.")
class PlayerViewPlayableCreator internal constructor(
  private val master: Master,
  private val bridgeCreatorFactory: PlayerViewBridgeCreatorFactory = defaultBridgeCreatorFactory
) : PlayableCreator<PlayerView>(PlayerView::class.java) {

  constructor(context: Context) : this(Master[context.applicationContext])

  companion object {

    // Only pass Application to this method.
    private val defaultBridgeCreatorFactory: PlayerViewBridgeCreatorFactory = { context ->
      // ExoPlayerProvider
      val playerPool = ExoPlayerPool(
        context = context,
        userAgent = Common.getUserAgent(context, BuildConfig.LIB_NAME)
      )
      PlayerViewBridgeCreator(playerPool, playerPool.defaultMediaSourceFactory)
    }
  }

  private val bridgeCreator: Lazy<BridgeCreator<PlayerView>> = lazy(NONE) {
    bridgeCreatorFactory(master.app)
  }

  override fun createPlayable(
    config: Config,
    media: Media
  ): Playable {
    return PlayerViewPlayable(
      master,
      media,
      config,
      bridgeCreator.value.createBridge(master.app, media)
    )
  }

  override fun cleanUp() {
    if (bridgeCreator.isInitialized()) bridgeCreator.value.cleanUp()
  }

  class Builder(context: Context) {

    private val app = context.applicationContext

    private var bridgeCreatorFactory: PlayerViewBridgeCreatorFactory = defaultBridgeCreatorFactory

    fun setBridgeCreatorFactory(factory: PlayerViewBridgeCreatorFactory): Builder = apply {
      this.bridgeCreatorFactory = factory
    }

    fun build(): PlayableCreator<PlayerView> = PlayerViewPlayableCreator(
      Master[app],
      bridgeCreatorFactory
    )
  }
}
