# react-native-picker

A Picker written in pure javascript for cross-platform support.

It was most likely an example of how to build a cross-platform Picker Component use [react-native-picker-android](https://github.com/beefe/react-native-picker-android).

Needs react-native >= 0.14.2

![ui](./doc/ui.gif)

###Documentation

####Props
- <b>pickerHeight</b> number
- <b>showDuration</b> number
- <b>pickerData</b> array
- <b>selectedValue</b> any
- <b>onPickerDone</b> function

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