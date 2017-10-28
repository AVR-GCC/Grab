import React, { Component } from 'react';
import { Text, View, Button, TouchableHighlight, ScrollView, Dimensions } from 'react-native';
import { CardItem } from './';


class ProviderChooser extends Component {

	state = { db: [] };

	constructor(props){
	    super(props);
	}

	componentWillMount(){
		this.setState({ db: this.props.db });
	}

	render(){
		return(
			<View style={styles.containerStyle}>
				<Text style={styles.titleStyle}>
					Choose Cleaner
				</Text>
				<CardItem>
					{this.renderBody()}
				</CardItem>
			</View>
		);
	}

	renderBody(){
		return this.state.db.map(val =>
			{this.renderProvider(val)}
		);
	}

	renderProvider(val){
		console.log(val.name);
		console.log(val.pic);
		return(
			<View key={val.name} style={styles.tabStyle} >
				<Image source={{uri: val.pic}} style={styles.ImageStyle} />
				<Text style={styles.titleStyle}>
					{val.name}
				</Text>
			</View>
		);
	}

}

const styles = {

	containerStyle: {
		height: 100,
		width: null,
		alignItems: 'center'
	},

	tabStyle: {
		alignItems: 'center',
		justifyContent: 'flex-start',
		flexDirection: 'row',
    	marginBottom: 5,
    	marginTop: 5
	},

	titleStyle: {
		fontSize: 15,
    	fontFamily: 'sans-serif-thin',
    	color: '#E1FCFF',
    	marginBottom: 5
	},

	ImageStyle: {
		marginTop: 5,
		marginBottom: 5,
		marginLeft: 5,
		marginRight: 5,
		height: 30,
		width: 30,
		borderColor: '#E1FCFF',
		borderWidth: 2
	}
};

export { ProviderChooser };