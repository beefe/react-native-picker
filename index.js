import {
    Platform,
    NativeModules,
    NativeAppEventEmitter
} from 'react-native';

const ios = Platform.OS === 'ios';
const android = Platform.OS === 'android';
const Picker = NativeModules.BEEPickerManager;
const options = {
    isLoop: false,
    pickerConfirmBtnText: 'confirm',
    pickerCancelBtnText: 'cancel',
    pickerTitleText: 'pls select',
    pickerConfirmBtnColor: [1, 186, 245, 1],
    pickerCancelBtnColor: [1, 186, 245, 1],
    pickerTitleColor: [20, 20, 20, 1],
    pickerToolBarBg: [232, 232, 232, 1],
    pickerTextEllipsisLen: 6,
    pickerBg: [196, 199, 206, 1],
    pickerRowHeight: 24,
    wheelFlex: [1, 1, 1],
    pickerData: [],
    selectedValue: [],
    onPickerConfirm(){},
    onPickerCancel(){},
    onPickerSelect(){},
    pickerToolBarFontSize: 16,
    pickerFontSize: 16,
    pickerFontColor: [31, 31 ,31, 1]
};

export default {
    init(params){
        const opt = {
            ...options,
            ...params
        };
        const fnConf = {
            confirm: opt.onPickerConfirm,
            cancel: opt.onPickerCancel,
            select: opt.onPickerSelect
        };

        Picker._init(opt);
        //there are no `removeListener` for NativeAppEventEmitter & DeviceEventEmitter
        this.listener && this.listener.remove();
        this.listener = NativeAppEventEmitter.addListener('pickerEvent', event => {
            fnConf[event['type']](event['selectedValue'], event['selectedIndex']);
        });
    },

    show(){
        Picker.show();
    },

    hide(){
        Picker.hide();
    },

    select(arr, fn) {
        if(ios){
            Picker.select(arr);
        }
        else if(android){
            Picker.select(arr, err => {
                typeof fn === 'function' && fn(err);
            });
        }
    },

    toggle(){
        this.isPickerShow(show => {
            if(show){
                this.hide();
            }
            else{
                this.show();
            }
        });
    },

    isPickerShow(fn){
        //android return two params: err(error massage) and status(show or not)
        //ios return only one param: hide or not...
        Picker.isPickerShow((err, status) => {
            let returnValue = null;
            if(android){
                returnValue = err ? false : status;
            }
            else if(ios){
                returnValue = !err;
            }
            fn && fn(returnValue);
        });
    }
};
