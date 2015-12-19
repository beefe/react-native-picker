# react-native-picker

A Picker written in pure javascript for cross-platform support.

It was most likely an example of how to build a cross-platform Picker Component use [react-native-picker-android](https://github.com/beefe/react-native-picker-android).

Needs react-native >= 0.14.2

![ui](./doc/ui.gif)

###Documentation

####Props
- <b>pickerBtnText</b> string, tool bar's btn text
- <b>pickerBtnStyle</b> textStylePropType, tool bar's btn style
- <b>pickerToolBarStyle</b> viewStylePropType, tool bar's style
- <b>pickerHeight</b> number
- <b>showDuration</b> number
- <b>pickerData</b> array
- <b>selectedValue</b> any
- <b>onPickerDone</b> function

####Methods
- <b>toggle</b> show or hide picker, default to be hiden

###Usage

####Step 1 - install

```
	npm install react-native-picker --save
```

####Step 2 - import and use in project

```javascript
	import Picker from 'react-native-picker'
	
	<Picker
		ref={picker => {this.picker = picker;}}
		pickerHeight={300}
		showDuration={300}
		pickerData={}//picker`s value List
		selectedValue={}//default to be selected value
		onPickerDone={}//when confirm your choice
	/>
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
	pickerData = {
		{
			a: [1,2,3,4],
			b: [5,6,7,8],
			...
		}
	};
	selectedValue = ['a', 2];
```

- three wheel

```javascript
	pickerData = {
		a: {
			a1: [1,2,3,4],
			a2: [5,6,7,8],
			a3: [9,10,11,12]
		},
		b: {
			b1: [1,2,3,4],
			b2: [5,6,7,8],
			b3: [9,10,12,12]
		}
		...
	};
	selectedValue = ['a', 'a1', 1];
```
