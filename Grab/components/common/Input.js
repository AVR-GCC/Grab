import React, { Component } from 'react';
import { View, Text, TextInput } from 'react-native';

const Input = ({ label, value, onChangeText, placeholder, secureTextEntry }) => {
  return (
    <View
    style={styles.containerStyle}>
      <TextInput
      secureTextEntry={secureTextEntry}
      placeholder={placeholder}
      onChangeText={onChangeText}
      style={styles.inputStyle}
      autoCorrect={false}
      />
      <Text
      style={styles.labelStyle}>
        {label}
      </Text>
    </View>
  );
};

const styles = {
  inputStyle: {
    color: '#FFFFFF',
    paddingRight: 5,
    paddingLeft: 5,
    fontSize: 18,
    lineHeight: 23,
    flex: 2
  },
  labelStyle: {
    fontSize: 18,
    fontFamily: 'sans-serif-thin',
    color: '#E1FCFF',
    paddingLeft: 20,
    flex: 1
  },
  containerStyle: {
    height: 40,
    flex: 1,
    flexDirection: 'row',
    alignItems: 'center'
  }
}

export { Input };
