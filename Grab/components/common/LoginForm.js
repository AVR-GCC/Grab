import React, { Component } from 'react';
import { View, TextInput, Text } from 'react-native';
//import firebase from 'firebase';
import { Card, CardItem, Button, Input, Spinner, WeakView } from './';

class LoginForm extends Component {
  state = { text: 'sign in', pw: '', pn: '', error: '', loading: false, loadingphonenum: false };

  onButtonPress() {
    this.setState({ error: '', loading: true });
    // firebase.auth().signInWithEmailAndPassword(this.state.text, this.state.pw)
    // .then(this.onLoginSuccess.bind(this))
    // .catch(()=> {
    //     firebase.auth().createUserWithEmailAndPassword(this.state.text, this.state.pw)
    //       .then(this.onLoginSuccess.bind(this))
    //       .catch(this.onLoginFail.bind(this));
    // });
  }

  onLoginSuccess() {
    this.setState({
      text: '___',
      pw: '',
      error: 'Success!',
      loading: false });
  }

  onLoginFail() {
    this.setState({
      error: 'Error!',
      loading: false
    });
  }

  renderButton() {
    if(this.state.loading){
      return(<Spinner size="small" />);
    }
    return(
      <Button
        shit='Sign in with email'
        onPress={this.onButtonPress.bind(this)}
        fontSize={20}
        />
      );
  }


  render(){
    return(
    <View>
        <Card>
          <CardItem>
            <Input
            label='Username'
            value={this.state.text}
            placeholder='example@email.com'
            onChangeText={text => this.setState({ text })}
            />
          </CardItem>
          <CardItem>
            <Input
            secureTextEntry
            label='Password'
            value={this.state.pw}
            placeholder='enter password'
            onChangeText={pw => this.setState({ pw })}
            />
          </CardItem>
          <CardItem>
            <Input
            label='Phone #'
            value={this.state.pn}
            placeholder='enter phone #'
            onChangeText={pn => this.setState({ pn })}
            />
          </CardItem>
          <Text
          style={styles.errorStyle}>
          {this.state.error}
          </Text>
          <CardItem>
            {this.renderButton()}
          </CardItem>
        </Card>
        <Prompt
            title="Confirm Phone Number"
            placeholder="Your SMS code"
            defaultValue="####"
            visible={false}
            onCancel={() => this.setState({ promptVisible: false })}
            onSubmit={(value) => this.setState({ promptVisible: false })}/>
    </View>
    );
  }
};

const styles = {
  errorStyle: {
    fontSize: 20,
    alignSelf: 'center',
    color: 'red'
  }
}

export { LoginForm };
