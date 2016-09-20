# react-native-picker

[![npm version](https://img.shields.io/npm/v/react-native-picker.svg?style=flat-square)](https://www.npmjs.com/package/react-native-picker) <a href="https://david-dm.org/beefe/react-native-picker"><img src="https://david-dm.org/beefe/react-native-picker.svg?style=flat-square" alt="dependency status"></a>  

A Native Picker with high performance

####Demo

- <b>[Date-picker](./demo/date-picker.js)</b>
- <b>[Area-picker](./demo/area-picker.js)</b>


![ui](./doc/ui.gif)

![ui2](./doc/ui2.jpg)

###Documentation

####Props
- <b>pickerConfirmBtnText</b> string, 确认按钮文字
- <b>pickerCancelBtnText</b> string, 取消按钮文字
- <b>pickerTitleText</b> string, 标题文字
- <b>pickerConfirmBtnColor</b> [1, 186, 245, 1],  确认按钮字体颜色
- <b>pickerCancelBtnColor</b> [1, 186, 245, 1],  取消按钮字体颜色
- <b>pickerTitleColor</b> [20, 20, 20, 1],  标题字体颜色
- <b>pickerToolBarBg</b> [232, 232, 232, 1],  工具栏背景颜色
- <b>pickerBg</b> [196, 199, 206, 1],  picker背景颜色
- <b>pickerData</b> 数组或对象，picker数据
- <b>selectedValue</b> string，默认选中数据
- <b>onPickerConfirm</b> function，确认按钮回调
- <b>onPickerCancel</b> function，取消按钮回调
- <b>onPickerSelect</b> function，滚轮滚动时回调

####Methods
- <b>toggle</b> show or hide picker, default to be hiden
- <b>show</b> show picker
- <b>hide</b> hide picker
- <b>isPickerShow</b> get status of picker, return a boolean

###Usage

####Step 1 - install

```
	npm install react-native-picker --save
```

####Step 2 - link

```
	react-native link
```

####Step 3 - import and use in project

```javascript
	import Picker from 'react-native-picker';

	let data = [];
    for(var i=0;i<100;i++){
        data.push(i);
    }

    Picker.init({
        pickerData: data,
        selectedValue: [59],
        onPickerConfirm: data => {
            console.log(data);
        },
        onPickerCancel: data => {
            console.log(data);
        },
        onPickerSelect: data => {
            console.log(data);
        }
    });
    Picker.show();
	
```

###Notice

####support two modes:

<b>1. parallel:</b> such as time picker, wheels have no connection with each other

<b>2. cascade:</b> such as date picker, address picker .etc, when front wheel changed, the behind wheels will all be reset

####parallel:

- single wheel:

```javascript
	pickerData = [1,2,3,4];
	selectedValue = 3;
```

- two or more wheel:

```javascript
	pickerData = [
		[1,2,3,4],
		[5,6,7,8],
		...
	];
	selectedValue = [1, 5];
```

####cascade:

- two wheel

```javascript
    pickerData = [
        {
            a: [1, 2, 3, 4]
        },
        {
            b: [5, 6, 7, 8]
        },
        ...
    ];
    selectedValue = ['a', 2];
```

- three wheel

```javascript
    pickerData = [
        {
            a: [
                {
                    a1: [1, 2, 3, 4]
                },
                {
                    a2: [5, 6, 7, 8]
                },
                {
                    a3: [9, 10, 11, 12]
                }
            ]
        },
        {
            b: [
                {
                    b1: [11, 22, 33, 44]
                },
                {
                    b2: [55, 66, 77, 88]
                },
                {
                    b3: [99, 1010, 1111, 1212]
                }
            ]
        },
        {
            c: [
                {
                    c1: ['a', 'b', 'c']
                },
                {
                    c2: ['aa', 'bb', 'cc']
                },
                {
                    c3: ['aaa', 'bbb', 'ccc']
                }
            ]
        },
        ...
    ]
```