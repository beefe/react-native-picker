import {
    Platform,
    NativeModules,
    NativeAppEventEmitter
} from 'react-native';

let ios = Platform.OS === 'ios';
let android = Platform.OS === 'android';
let Picker = NativeModules.BEEPickerManager;

export default {

    init(options){
        let opt = {
            isLoop: false,
            pickerConfirmBtnText: '确认',
            pickerCancelBtnText: '取消',
            pickerTitleText: '请选择',
            pickerBg: [196, 199, 206, 1],
            pickerToolBarBg: [232, 232, 232, 1],
            pickerTitleColor: [20, 20, 20, 1],
            pickerCancelBtnColor: [1, 186, 245, 1],
            pickerConfirmBtnColor: [1, 186, 245, 1],
            onPickerConfirm(){},
            onPickerCancel(){},
            onPickerSelect(){},
            ...options
        };
        let fnConf = {
            confirm: opt.onPickerConfirm,
            cancel: opt.onPickerCancel,
            select: opt.onPickerSelect
        };

        Picker._init(opt);
        //there are no `removeListener` for NativeAppEventEmitter & DeviceEventEmitter
        this.listener && this.listener.remove();
        this.listener = NativeAppEventEmitter.addListener('pickerEvent', event => {
            if(ios){
                fnConf[event['type']](event['selectedValue']);
            }
            else if(android){
                for (let i in event){
                    typeof fnConf[i] === 'function' && fnConf[i](event[i]);
                }
            }
        });
    },

    show(){
        Picker.show();
    },

    hide(){
        Picker.hide();
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
            fn(returnValue);
        });
    }
};