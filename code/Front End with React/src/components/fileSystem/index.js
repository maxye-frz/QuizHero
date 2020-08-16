import React, { Fragment } from 'react';
import { BrowserRouter as Router } from 'react-router-dom';
import { render } from 'react-dom';
import { Provider } from 'react-redux';
import { createStore } from 'redux';
import { composeWithDevTools } from 'redux-devtools-extension';

import { BrowserRouter } from 'react-router-dom';
import Sidebar from './src/components/Sidebar';

import './src/assets/styles/App.scss';

import reducers from './src/reducers';
import { ViewFiles } from './src/pages';

import generateFileSystem from './src/utils/generateFileSystem';

const fileSystem = generateFileSystem();
setTimeout(function () {
    if (fileSystem) {
        console.log(fileSystem);

    }
}, 5000);
const store = createStore(
  reducers,
  {
    fileSystem:
        // refreshCallback()
      // generateFileSystem()
      localStorage.getItem('fileSystem') &&
      Object.keys(localStorage.getItem('fileSystem')).length > 0
        ? JSON.parse(localStorage.getItem('fileSystem'))
        : generateFileSystem()
  },
  composeWithDevTools()
);

const App = () => (
  <Provider store={store}>
    <Router>
      <BrowserRouter>
        <Fragment>
          <Sidebar />
          <ViewFiles />
        </Fragment>
      </BrowserRouter>
    </Router>
  </Provider>
);

export default App;
