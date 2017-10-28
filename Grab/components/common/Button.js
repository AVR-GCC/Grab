import React from 'react';
import { Text, TouchableOpacity } from 'react-native';

const Button = (props) => {
  var { height, fontSize } = props;
  if(!height)
    height = fontSize + 30;
  return (
    <TouchableOpacity
    onPress={props.onPress}
    style={[styles.buttonStyle, { height }]}>
      <Text style={[styles.textStyle,  { fontSize }]}>{props.shit}</Text>
    </TouchableOpacity>
  );
};

const styles = {
  buttonStyle: {
    flex: 1,
    alignSelf: 'stretch',
    backgroundColor: '#000000',
    borderWidth: 1,
    borderColor: '#E1FCFF',
    marginLeft: 5,
    marginRight: 5
  },

  textStyle: {
    flex: 1,
    alignSelf: 'center',
    fontWeight: '600',
    paddingTop: 10,
    fontFamily: 'sans-serif-thin',
    color: '#E1FCFF',
    paddingBottom: 10
  }
};

export { Button };
