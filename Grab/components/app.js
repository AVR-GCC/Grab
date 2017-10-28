import React, { Component } from 'react';
import { View, Text, Image, ScrollView, TouchableHighlight, Alert } from 'react-native';
import { BleManager } from 'react-native-ble-plx';
import { Header, Card, CardItem, Button, Spinner, WeakView, ProviderChooser, Input } from './common';

class Grab extends Component {
  state = { price: 0, rate: 0, error: '', devices: ['fakedev1', 'fakedev2', 'realdevjk'], scanning: false };

  constructor() {
      super();
      this.manager = new BleManager();
  }

  componentWillMount() {
    // const subscription = this.manager.onStateChange((state) => { // for IOS
    //     if (state === 'PoweredOn') {
    //         this.scanAndConnect();
    //         subscription.remove();
    //     }
    // }, true);
  }

  onButtonConsumePress() {
    console.log("onButtonConsumePress");
    this.scanAndConnect();
  }

  onButtonProvidePress() {
    console.log("onButtonProvidePress");
  }

  scanAndConnect() {
    console.log("scanAndConnect");
    if(!this.state.scanning) {
      this.setState({ scanning: true });
      this.manager.startDeviceScan(null, null, (error, device) => {
        if (error) {
            // Handle error (scanning will be stopped automatically)
            console.log(error);
            this.setState({ error });
            return;
        }
        console.log("no error");
        var devs = this.state.devices;
        devs.push(device);
        console.log(device.name)
        device.connect()
          .then((dev) => {
              return dev.discoverAllServicesAndCharacteristics()
          })
          .then((dev) => {
             // Do work on device with services and characteristics
             console.log(dev.name)
          })
          .catch((er) => {
              // Handle errors
              console.log(er)
          });
        this.setState({ devices: devs, error: device.name });
        // Check if it is a device you are looking for based on advertisement data
        // or other criteria.
        // if (device.name === 'TI BLE Sensor Tag' || 
        //     device.name === 'SensorTag') {
            
        //     // Stop scanning as it's not necessary if you are scanning for one device.
        //     this.manager.stopDeviceScan();

        //     // Proceed with connection.
        // }
      });
    }
    else {
      this.stopScan();
    }
  }

  stopScan() {
    console.log("stopScan");
    this.setState({ scanning: false });
    this.manager.stopDeviceScan();
  }

  renderDevicesChooser() {
    console.log("renderDevicesChooser");
    return this.state.devices.map(dev => 
      <Button
        shit={dev.name}
        onPress={this.onButtonProvidePress.bind(this)}
        fontSize={20}
        />
    );
  }

  renderDevicesChooserTop() {
    if(this.state.scanning){
      return (
        <View>
          {this.renderDevicesChooser()}
          <Spinner />
        </View>
      );
    }
    return (
      <View>
        {this.renderDevicesChooser()}
      </View>
    );
  }

  render() {
    console.log("render");
    if(this.state.scanning){
      var consumeString = 'Stop Scan';
    }
    else {
      var consumeString = 'Scan Providers';
    }
    return (
      <View style={{ backgroundColor: '#000000', flex: 1 }}>
        <Header ht={'Grab'} bg={'#2E5090'} />
        <View style={{ alignItems: 'center' }}>
          <Image source={{uri: 'https://image.ibb.co/euf4Vb/Grab_Logo3.jpg'}} style={styles.ImageStyle} />
        </View>
        <Card>
          <CardItem>
            <Input
            label='Price mIOTA'
            value={this.state.price}
            placeholder='enter your price'
            onChangeText={price => this.setState({ price })}
            />
          </CardItem>
          <CardItem>
            <Input
            label='Rate tran/min'
            value={this.state.rate}
            placeholder='enter your rate'
            onChangeText={rate => this.setState({ rate })}
            />
          </CardItem>
          <CardItem>

            <View style={styles.buttonViewStyle}>
              <Button
              shit={consumeString}
              onPress={this.onButtonConsumePress.bind(this)}
              fontSize={15}
              />
              <Button
              shit='Provide'
              onPress={this.onButtonProvidePress.bind(this)}
              fontSize={15}
              />
            </View>
          </CardItem>
        </Card>
        <View style={{ backgroundColor: '#000000', flex: 1 }}>
          {this.renderDevicesChooserTop()}
        </View>
      </View>
    );
  }
}

const styles = {
  viewStyle: {
    justifyContent: 'center',
    alignItems: 'center',
    height: 70,
    paddingTop: 15,
    elevation: 2,
    position: 'relative'
  },

  buttonViewStyle: {
    flex: 1,
    flexDirection: 'row',
    justifyContent: 'center',
    alignItems: 'center'
  },

  textStyle: {
    fontSize: 20,
    fontFamily: 'sans-serif-thin',
    color: '#E1FCFF'
  },

  nextlastWeakText: {
    fontSize: 30,
    fontFamily: 'sans-serif-thin',
    color: '#E1FCFF'
  },

  ProviderTitleStyle: {
    fontSize: 15,
    fontFamily: 'sans-serif-thin',
    color: '#E1FCFF',
    paddingTop: 5,
    paddingBottom: 5,
    paddingLeft: 5,
    paddingRight: 5,
  },

  containerStyle: {
    height: 300,
    width: null,
    alignItems: 'center'
  },

  weekButtonStyle: {
    height: 110,
    width: 15,
    justifyContent: 'center',
    alignItems: 'center'
  },

  ListContainerStyle: {
    height: null,
    width: 400,
    justifyContent: 'flex-start',
    alignItems: 'flex-end',
    borderColor: '#E1FCFF',
    borderWidth: 2,
    borderTopWidth: 0
  },


  tabStyle: {
    alignItems: 'center',
    justifyContent: 'flex-end',
    flexDirection: 'row',
    marginBottom: 5,
    marginTop: 5
  },

  ImageStyle: {
    marginTop: 15,
    marginBottom: 8,
    height: 180,
    width: 180
  },

  ImageSmallStyle: {
    marginTop: 5,
    marginBottom: 5,
    marginLeft: 10,
    marginRight: 10,
    height: 40,
    width: 40,
    borderColor: '#E1FCFF',
    borderWidth: 2

  },

  listHolder: {
    flexDirection: 'column', 
    alignItems: 'center',
    width: null,
  },

  dayStyle: {
    alignItems: 'center',
    justifyContent: 'space-around',
    flexDirection: 'row',
      marginBottom: 5,
      borderBottomColor: '#E1FCFF',
      borderBottomWidth: 1,
      borderTopWidth: 1,
      borderRightWidth: 1,
      borderLeftWidth: 1,
  },

  shiftStyle: {
    fontSize: 15,
      fontFamily: 'sans-serif-thin',
      color: '#E1FCFF',
      marginTop: 3,
      marginBottom: 3,
      marginLeft: 10,
      marginRight: 10
  }
};

export default Grab;
