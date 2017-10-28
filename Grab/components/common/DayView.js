import React, { Component } from 'react';
import { Text, View, Button, ScrollView } from 'react-native';

class DayView extends Component {

	state = { skeg: [], index: 0, date: '' };

	constructor(props){
	    super(props);
	}

	componentWillMount(){
		// var arr = [];
		// for(let i = 0; i < 48; i++){
		// 	arr.push(i%2);
		// }
		this.setState({ skeg: this.props.skg, index: this.props.index, date: this.props.date, width: this.props.width, height: this.props.height });
	}

	renderBlock(occupied, selected){
		var bgCol = '#444444';
		if(!occupied){
			if(selected){
				bgCol = '#f0ff00';
			}
			else{
				bgCol = '#333333';
			}
		}
		return(
			<View style={styles.blockStyle} backgroundColor={bgCol} width={this.state.width} height={this.state.height/36}>
			</View>
		);
	}

	


	renderDay(){
		return this.state.skeg.map(val =>
			<View key={val.index} style={styles.containerStyle}>
				{this.renderBlock(val.occupied, val.selected)}
			</View>
		);
	}

	render(){
		return(
			<View style={styles.containerStyle}>
				<Text style={styles.titleStyle}>
					{this.state.date}
				</Text>
				<View style={styles.containerStyle}>
					{this.renderDay()}
				</View>
			</View>
		);
	}
}

const styles = {

	containerStyle: {
		flexDirection: 'column',
        justifyContent: 'center',
        alignItems: 'center'
	},

	blockStyle: {
		height: null,
		width: null,
		marginBottom: 1,
		marginLeft: 2,
		marginRight: 2,
		marginTop: 1
	},

	titleStyle: {
		fontSize: 15,
    	fontFamily: 'sans-serif-thin',
    	color: '#E1FCFF',
    	marginBottom: 2
	},

	markTextStyle: {
		fontSize: 7,
    	fontFamily: 'sans-serif-thin',
    	color: '#E1FCFF',
    	marginTop: 4,
    	marginBottom: 4
	}
}

export { DayView };