/**
 * AppRouter route the application between different pages.
 * It is a combination of PublicRouter (do not require login) and PrivateRouter (require login)
 */

import React, { Component } from "react";
import { createBrowserHistory } from "history";
import { Switch, Router } from "react-router-dom";
import IndexPage from "../pages/IndexPage";
import LoginPage from "../pages/LoginPage";
import RegisterPage from "../pages/RegisterPage";
import { connect } from "react-redux";
import MyUpload from "../lagacy/UploadPage";
import PresentPage from '../pages/PresentPage'
import StudentPage from "../pages/StudentPage";
import StudentRequestPage from "../pages/StudentRequestPage";
import HomePage from "../pages/HomePage"
import PrivateRoute from "./PrivateRoute"
import PublicRoute from "./PublicRoute"
import EditPage from "../pages/EditPage";
import Select from "../components/home_componets/test";
import MyControl from "../components/home_componets/popup";
import fileSystemRouter from "../components/fileSystem/index";

const history = createBrowserHistory();

/**
 * Map the state from reducer to the props of the component
 * to get the data from the store
 *
 * @param {object}  state from reducer
 */
const mapStateToProps = state =>{
  return{
    instructorId: state.setUserName.instructorId
  }
}

/**
 * Frontend Router Component
 *
 */
class AppRouter extends Component {
  render() {
    // const {instructorId} = this.props;
    return (
      <Router history={history}>
      <Switch>
          <PublicRoute restricted={false} component={IndexPage} path="/" exact />
          <PublicRoute restricted={false} component={LoginPage} path="/login" exact />
          <PublicRoute restricted={false} component={RegisterPage} path="/register" exact />
          <PublicRoute restricted={false} component={StudentRequestPage} path="/StudentRequestPage" exact />
          <PublicRoute restricted={false} component={StudentPage} path="/student" exact />
          <PrivateRoute component={PresentPage} path="/presenter" exact />
          <PrivateRoute component={HomePage} path="/HomePage" exact />
          <PrivateRoute component={EditPage} path="/EditPage" exact />
          {/*<PrivateRoute component={Select} path="/Select" exact />*/}
          {/*<PrivateRoute component={MyControl} path="/Popup" exact />*/}
          <PrivateRoute component={fileSystemRouter} path="/fileSystem" exact />
          <fileSystemRouter path="/fileSystem" exact />
          <PrivateRoute component={MyUpload} path="/UploadPage" exact />
          <PrivateRoute component={HomePage} path="/HistoryPage" exact />
      </Switch>
      </Router>
     
    );
  }
}

export default connect(mapStateToProps)(AppRouter)