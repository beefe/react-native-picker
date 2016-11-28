import {
  Platform,
  NativeModules,
  NativeAppEventEmitter,
} from 'react-native';

const isIos = Platform.OS === 'ios';
const isAndroid = Platform.OS === 'android';
const Picker = NativeModules.BEEPickerManager;

const NOOP = () => {};

class ReactNativePicker {
  listener = null;

  init(options) {
    const fullOptions = {
      isLoop: false,
      pickerConfirmBtnText: 'Confirm',
      pickerCancelBtnText: 'Cancel',
      pickerTitleText: '',
      pickerBg: [196, 199, 206, 1],
      pickerToolBarBg: [232, 232, 232, 1],
      pickerTitleColor: [20, 20, 20, 1],
      pickerCancelBtnColor: [1, 186, 245, 1],
      pickerConfirmBtnColor: [1, 186, 245, 1],
      onPickerConfirm: NOOP,
      onPickerCancel: NOOP,
      onPickerSelect: NOOP,
      ...options,
    };

    const pickerFunctions = {
      confirm: fullOptions.onPickerConfirm,
      cancel: fullOptions.onPickerCancel,
      select: fullOptions.onPickerSelect,
    };

    Picker._init(fullOptions);

    this.listener && this.listener.remove();
    this.listener = NativeAppEventEmitter.addListener('pickerEvent', (events) => {
      if (isIos) {
        pickerFunctions[events['type']](events['selectedValue']);
      }
      else if (isAndroid) {
        for (let key in events) {
          typeof pickerFunctions[key] === 'function' && pickerFunctions[key](events[key]);
        }
      }
    });
  }

  show() {
    Picker.show();
  }

  hide() {
    Picker.hide();
  }

  toggle() {
    this.isPickerShow((show) => {
      show ? this.hide() : this.show();
    });
  }

  isPickerShow(callback) {
    /* Android returns two params - err and status, iOS returns only status. */
    Picker.isPickerShow((err, status) => {
      let returnValue = null;

      if (isIos) {
        returnValue = !err;
      } else if (isAndroid) {
        returnValue = err ? false : status;
      }

      callback(returnValue);
    });
  }
};

export default new ReactNativePicker();
