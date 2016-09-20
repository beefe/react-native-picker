/**
 * Bootstrap of PickerTest
 */

import React, {Component} from 'react';
import {
    View,
    Text,
    TouchableOpacity,
    Dimensions
} from 'react-native';

import Picker from 'react-native-picker';

function createDateData(){
    let date = {};
    for(let i=1950;i<2050;i++){
        let month = {};
        for(let j = 1;j<13;j++){
            let day = [];
            if(j === 2){
                for(let k=1;k<29;k++){
                    day.push(k+'日');
                }
            }
            else if(j in {1:1, 3:1, 5:1, 7:1, 8:1, 10:1, 12:1}){
                for(let k=1;k<32;k++){
                    day.push(k+'日');
                }
            }
            else{
                for(let k=1;k<31;k++){
                    day.push(k+'日');
                }
            }
            month[j+'月'] = day;
        }
        date[i+'年'] = month;
    }
    return date;
};

function createAreaData(callback){
    fetch('https://raw.githubusercontent.com/beefe/react-native-picker/master/example/PickerTest/area.json').then(res => {
        res.json().then(area => {
            let data = {};
            let len = area.length;
            for(let i=0;i<len;i++){
                let city = area[i]['city'];
                let cityLen = city.length;
                let ProvinceName = area[i]['name'];
                data[ProvinceName] = {};
                for(let j=0;j<cityLen;j++){
                    let area = city[j]['area'];
                    let cityName = city[j]['name'];
                    data[ProvinceName][cityName] = area;
                }
            }
            callback(data);
        });
    }, err => {
        console.log(err);
    });
};

export default class PickerTest extends Component {

    constructor(props, context) {
        super(props, context);
    }

    _showDatePicker() {
        Picker.hide();
        Picker.init({
            pickerData: createDateData(),
            selectedValue: ['2015年', '12月', '12日'],
            onPickerConfirm: pickedValue => {
                console.log('date', pickedValue);
            },
            onPickerCancel: pickedValue => {
                console.log('date', pickedValue);
            },
            onPickerSelect: pickedValue => {
                console.log('date', pickedValue);
            }
        });
        Picker.show();
    }

    _showAreaPicker() {
        createAreaData(data => {
            Picker.init({
                pickerData: data,
                selectedValue: ['北京', '北京', '朝阳区'],
                onPickerConfirm: pickedValue => {
                    console.log('area', pickedValue);
                },
                onPickerCancel: pickedValue => {
                    console.log('area', pickedValue);
                },
                onPickerSelect: pickedValue => {
                    console.log('area', pickedValue);
                }
            });
            Picker.show();
        });
    }

    render() {
        return (
            <View style={{height: Dimensions.get('window').height}}>
                <TouchableOpacity style={{marginTop: 40, marginLeft: 20}} onPress={this._showDatePicker.bind(this)}>
                    <Text>DatePicker</Text>
                </TouchableOpacity>
                <TouchableOpacity style={{marginTop: 10, marginLeft: 20}} onPress={this._showAreaPicker.bind(this)}>
                    <Text>AreaPicker</Text>
                </TouchableOpacity>
            </View>
        );
    }
};
