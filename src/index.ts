import { NativeModules, NativeEventEmitter } from 'react-native';

const { FindBackgroundLocationVD } = NativeModules;

if (!FindBackgroundLocationVD) {
  throw new Error(
    'react-native-find-background-location-vd is not linked correctly'
  );
}

const emitter = new NativeEventEmitter(FindBackgroundLocationVD);

export type LocationPayload = {
  latitude: number;
  longitude: number;
};

/**
 * Start background location tracking
 */
export const startLocation = (): void => {
  FindBackgroundLocationVD.start();
};

/**
 * Stop background location tracking
 */
export const stopLocation = (): void => {
  FindBackgroundLocationVD.stop();
};

/**
 * Listen to background location updates
 */
export const onLocation = (callback: (data: LocationPayload) => void) => {
  return emitter.addListener('onLocation', (event: any) => {
    callback({
      latitude: event.latitude,
      longitude: event.longitude,
    });
  });
};
