/*
 * Copyright (c) 2018 Nam Nguyen, nam@ene.im
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

package kohii.v1.exo;

import android.support.annotation.NonNull;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.RenderersFactory;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;

/**
 * @author eneim (2018/06/25).
 */
final class DefaultPlayerFactory implements PlayerFactory {

  @SuppressWarnings({ "FieldCanBeLocal", "unused" })  //
  private final ExoStore store;
  private final Config config;

  private final RenderersFactory renderersFactory;  // stateless
  private final TrackSelector trackSelector;  // 'maybe' stateless
  private final LoadControl loadControl;  // stateless
  private final DrmSessionManagerFactory drmSessionManagerFactory;

  DefaultPlayerFactory(ExoStore store, Config config) {
    this.store = store;
    this.config = config;

    trackSelector = new DefaultTrackSelector(config.getMeter());
    loadControl = new DefaultLoadControl();
    renderersFactory = new DefaultRenderersFactory(store.context, config.getExtensionMode());
    drmSessionManagerFactory = new DefaultDrmSessionManagerFactory(store);
  }

  @NonNull @Override public Player createPlayer() {
    return new KohiiPlayer(renderersFactory, trackSelector, loadControl,
        drmSessionManagerFactory.createDrmSessionManager(this.config.getMediaDrm()));
  }
}
