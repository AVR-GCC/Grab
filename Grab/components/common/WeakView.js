import React, { Component } from 'react';
import { Text, View, Button, TouchableHighlight, ScrollView, Dimensions } from 'react-native';
//import firebase from 'firebase';


class WeakView extends Component {

	state = { skeg: [], wwidth: 0, wheight: 0, rulerlist: [], months: [], months: [{days: 31, name: 'Jan'}, {days: 28, name: 'Feb'}, {days: 31, name: 'Mar'}, {days: 30, name: 'Apr'}, {days: 31, name: 'May'}, {days: 30, name: 'Jun'}, {days: 31, name: 'Jul'}, {days: 31, name: 'Aug'}, {days: 30, name: 'Sep'}, {days: 31, name: 'Oct'}, {days: 30, name: 'Nov'}, {days: 31, name: 'Dec'}] };
	
	constructor(props){
	    super(props);
	}

	componentWillMount(){
		var wwidth = Dimensions.get('window').width;
		var wheight = Dimensions.get('window').height;
		var arr = [];
		
		var temp = [];
		for(let j = 0; j < 7; j++){
			temp.push({ index: j });
		}
		console.log(this.props.datestring);
		this.setState({ skeg: arr, rulerlist: temp ,wwidth: wwidth, wheight: wheight });
	}

	render(){
		return(
			<View style={styles.containerStyle}>
				<Text style={styles.titleStyle}>{this.props.datestring}</Text>
				<View style={styles.bodyStyle}>
					<View>{this.renderRuler(false)}</View>
					<View>{this.renderBody()}</View>
					<View>{this.renderRuler(true)}</View>
				</View>
			</View>
		);
	}

	parseDate(index){
		var datePieces = this.props.datestring.split('/');
		var day = Number.parseInt(datePieces[0], 10);
		var monthIndex = 1;
		day += index;
		if(day > this.state.months[Number.parseInt(datePieces[1], 10) - 1].days){
			day -= this.state.months[Number.parseInt(datePieces[1], 10) - 1].days;
			monthIndex = 3;
		}
		var res = day + '/' + datePieces[monthIndex];
		return(res);
	}



	renderBody(){
		return (this.props.children);
	}

	renderRuler(isDate){
		if(isDate){
			return this.state.rulerlist.map(val =>
				<View key={val.index}>
					{this.renderMarxName(val.index)}
				</View>
			);
		}
		else{
			return this.state.rulerlist.map(val =>
				<View key={val.index}>
					{this.renderMarxDate(val.index)}
				</View>
			);
		}
	}

	renderMarxName(index){
		var days = ['Sunday', 'Monday', 'Tuesday', 'Wednsday', 'Thursday', 'Friday', 'Saturday'];

		return(
			<View>
				<View style={{ height: 1 }} />
				<View>
					<Text style={styles.markTextStyle}>{days[index]}</Text>
				</View>
			</View>
		);
	}

	renderMarxDate(index){
		return(
			<View>
				<View style={{ height: 1 }} />
				<View>
					<Text style={styles.markTextStyle}>{this.parseDate(index)}</Text>
				</View>
			</View>
		);
	}


}

// renderAlbums() {
//     return this.state.albums.map(album =>
//        <AlbumDetail key={album.title} album={album} />
//      );
//   }

const styles = {

	containerStyle: {
		height: null,
		width: null,
		alignItems: 'center'
	},

	bodyStyle: {
		alignItems: 'center',
		justifyContent: 'flex-end',
		flexDirection: 'row',
    	marginBottom: 5
	},
	
	spacerStyle: {
		height: 20,
		width: 40
	},

	titleStyle: {
		fontSize: 15,
    	fontFamily: 'sans-serif-thin',
    	color: '#E1FCFF',
    	marginBottom: 5
	},


	hoursStyle: {
		fontSize: 15,
    	fontFamily: 'sans-serif-thin',
    	color: '#E1FCFF',
    	marginBottom: 5
	},

	markTextStyle: {
		fontSize: 10,
    	fontFamily: 'sans-serif-thin',
    	color: '#E1FCFF',
    	marginTop: 2,
    	marginBottom: 21,
    	marginLeft: 12,
    	marginRight: 10
	},

	
};

export { WeakView };