import React from 'react';
import { View } from 'react-native';

const CardItem = (props) => {
  return (
    <View style={styles.ItemStyle}>
      {props.children}
    </View>
  );
};

const styles = {
  ItemStyle: {
    padding: 5,
    backgroundColor: '#000000',
    justifyContent: 'flex-end',
    flexDirection: 'row',
    borderColor: '#E1FCFF',
    position: 'relative',
    borderBottomWidth: 1
  }
};

export { CardItem };
