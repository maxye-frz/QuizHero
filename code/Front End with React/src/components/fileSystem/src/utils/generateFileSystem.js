import jwt_decode from "jwt-decode";
import cookie from "react-cookies";
import axios from "axios";
import {BASE_URL} from "../../../../config/config";

const loginInfo = jwt_decode(cookie.load('token'));

const username = loginInfo["name"]
// const username = "yaozixuan";
let fileSystem = {
  '1382b6993e9f270cb1c29833be3f5750': {
    type: '__folder__',
    name: 'root',
    path: '/',
    size: 0,
    // date: '2019-04-07',
    creatorName: username,
    parentPath: null,
    parentID: null,
    children: []
  },
  '9b6739960c1ac83251046da2c718019b': {
    type: '__folder__',
    name: 'new folder',
    creatorName: username,
    // size: 223,
    // date: '2019-04-29',
    parentID: '1382b6993e9f270cb1c29833be3f5750',
    parentPath: '/',
    path: '/apps',
    children: []
  }
};

const generateFileSystem = () => {

  let params = {
    userId : loginInfo['userId']
    // userId : 1
  }
  // console.log(params)
  axios
      .get(BASE_URL + "/history", {params})
      .then((res) => {
        if(res.status === 200){
          console.log("res",res.data);
          return convertFileIdList2FileSystem(res.data);
        }
      })
      .catch((error) => {
        console.log(error)
      });


  // localStorage.setItem('fileSystem', JSON.stringify(fileSystem));
  // return fileSystem;
};

const convertFileIdList2FileSystem = (fileList) => {
  let fileIdList = ['9b6739960c1ac83251046da2c718019b'];
  fileList.forEach((file, index, array) => {
    fileIdList.push(file.fileId)
  });

  // console.log(fileList);
  fileSystem['1382b6993e9f270cb1c29833be3f5750'].children = fileIdList;
  fileList.forEach((file, index, array) => {
    fileSystem[file.fileId] = {
      type: '__file__',
      name: file.fileName,
      creatorName: username,
      parentID: '1382b6993e9f270cb1c29833be3f5750',
      parentPath: '/',
      path: '/' + file.fileName
    }
  });

  console.log(fileSystem);
  localStorage.setItem('fileSystem', JSON.stringify(fileSystem));
  return fileSystem;
}

export default generateFileSystem;
