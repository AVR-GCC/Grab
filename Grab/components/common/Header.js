import React from 'react';
import { Text, View } from 'react-native';

const Header = (props) => {
  const { textStyle } = styles;
  const { viewStyle } = styles;
  return (
    <View style={viewStyle} backgroundColor={props.bg}>
      <Text style={textStyle}>{props.ht}</Text>
    </View>
  );
};

const styles = {
  viewStyle: {

    justifyContent: 'center',
    alignItems: 'center',
    height: 40,
    paddingTop: 15,
    elevation: 2,
    position: 'relative'
  },

  textStyle: {
    fontSize: 20,
    fontFamily: 'sans-serif-thin',
    color: '#E1FCFF'
  }
};
export { Header };
