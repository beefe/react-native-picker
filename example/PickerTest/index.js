/**
 * Bootstrap of PickerTest
 */
import React from 'react';
import {
  View,
  Text,
  TouchableOpacity,
  Dimensions,
  AppRegistry,
} from 'react-native';
import Picker from 'react-native-picker';

import area from './area.json';

const monthNames = [
  'January', 'February', 'March',
  'April', 'May', 'June',
  'July', 'August', 'September',
  'October', 'November', 'December',
];

const createDaysData = (year, month) => {
  const monthLength = new Date(year, month + 1, 0).getDate();
  const days = [...Array(monthLength)].map((_, i) => i + 1);

  return days;
};

const createDateData = () => {
  const yearsData = [];
  for (let year = 2000; year <= 2030; year += 1) {
    const monthsData = [];
    for (let month = 0; month < 12; month += 1) {
      const monthObject = {};
      monthObject[monthNames[month]] = createDaysData(year, month);

      monthsData[monthsData.length] = monthObject;
    }

    const yearObject = {};
    yearObject[year] = monthsData;

    yearsData[yearsData.length] = yearObject;
  }

  return yearsData;
};

const createAreaData = () => {
  const data = [];

  for (let i = 0; i < area.length; i += 1) {
    const city = [];
    for (let j = 0; j < area[i]['city'].length; j += 1) {
      const cityObject = {};
      cityObject[area[i]['city'][j]['name']] = area[i]['city'][j]['area'];

      city[city.length] = cityObject;
    }
    const dataObject = {};
    dataObject[area[i]['name']] = city;

    data[data.length] = dataObject;
  }

  return data;
};

const initPicker = (pickerData, selectedValue) => {
  Picker.init({
    pickerData,
    selectedValue,
    onPickerConfirm: (value) => {
        console.log('confirmed', value);
    },
    onPickerCancel: (value) => {
        console.log('cancelled', value);
    },
    onPickerSelect: (value) => {
        console.log('selected', value);
    },
  });
}

export default class PickerTest extends React.Component {

  constructor(props, context) {
    super(props, context);
  }

  showDatePicker = () => {
    initPicker(createDateData(), ['2016', 'August', '4']);
    Picker.show();
  }

  showAreaPicker = () => {
    initPicker(createAreaData(), ['2016', 'August', '4']);
    Picker.show();
  }

  toggle = () => {
    Picker.toggle();
  }

  isPickerShow = () => {
    Picker.isPickerShow((status) => {
      alert(status);
    });
  }

  render() {
    return (
      <View>
        <Button label="Date Picker" onPress={this.showDatePicker} />
        <Button label="Area Picker" onPress={this.showAreaPicker} />
        <Button label="Toggle" onPress={this.toggle} />
        <Button label="isPickerShow" onPress={this.isPickerShow} />
      </View>
    );
  }
};

const Button = ({ onPress, label }) => (
  <TouchableOpacity style={{ marginTop: 10, marginLeft: 20 }} onPress={onPress}>
    <Text>{label}</Text>
  </TouchableOpacity>
);

AppRegistry.registerComponent('PickerTest', () => PickerTest);
