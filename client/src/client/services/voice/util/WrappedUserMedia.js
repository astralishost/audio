import {premadeAudioStream} from "../../../../views/login/platforms/bedrock/BedrockAuthFlow";
import {debugLog} from "../../debugging/DebugService";

export class WrappedUserMedia {

    constructor() {
        this.successCallback = alert;
        this.errorCallback = alert;
    }

    getUserMedia(preferedDeviceId = null) {

        // do we already have a mic stream from a flow?
        if (premadeAudioStream) {
            let isHealthy = true;
            // check if the stream is dead
            if (premadeAudioStream.getAudioTracks().length === 0) {
                isHealthy = false;
            } else {
                // check if at least one stream is active
                for (let track of premadeAudioStream.getAudioTracks()) {
                    if (!track.enabled) {
                        isHealthy = false;
                        break;
                    }
                }
            }

            if (isHealthy) {
                this.successCallback(premadeAudioStream);
                debugLog("Using premade audio stream, leaving WrappedUserMedia.getUserMedia() prematurely")
                return;
            }
        }

        let argument = {
            audio: {
                    noiseSuppression: true,
                    // sampleRate: 64000,
                    echoCancellation: true,
                    autoGainControl: false,
                    channelCount: 1,
                }
        }

        if (preferedDeviceId) {
            argument.audio.deviceId = {exact: preferedDeviceId}
        }

        if (navigator.getUserMedia != null) {
            navigator.getUserMedia(argument, this.successCallback, this.errorCallback);
            return;
        }

        if (navigator.webkitGetUserMedia != null) {
            navigator.webkitGetUserMedia(argument, this.successCallback, this.errorCallback);
            return;
        }

        if (navigator.mediaDevices.getUserMedia != null) {
            navigator.mediaDevices.getUserMedia(argument)
                .then(hasStream => this.successCallback(hasStream))
                .catch(error => this.errorCallback(error));
            return;
        }

        if (navigator.msGetUserMedia != null) {
            navigator.msGetUserMedia(argument, this.successCallback, this.errorCallback);
            return;
        }

        console.error("Unknown user media platform!");
    }

}

