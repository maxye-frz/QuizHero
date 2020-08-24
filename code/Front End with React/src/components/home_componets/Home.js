import UploadNew from "./UploadNew";
import History from "./History";
import FileListContext from "./fileListContext";
import React, {useState} from "react";
// import {useCookies} from "react-cookie";
// import jwt from "jwt-decode";
import styled from "styled-components";
import axios from "axios";
import {BASE_URL} from "../../config/config";
import jwt_decode from "jwt-decode";
import cookie from "react-cookies";
import MyControl from "./popup";

const ContainerLeft = styled.div`
  width: 20%;
  height: calc(100vh - 60px);
  padding: 13px;
  border-right: 1.5px solid rgba(15, 15, 15, 0.4);
  // font-family: "Lato", sans-serif;
`;
const ContainerRight = styled.div`
  width: 80%;
  height: calc(100vh - 60px);
  padding: 13px;
  // font-family: "Lato", sans-serif;
`;


export default function Home(props) {

    const [fileList, setFileList] = useState([]);
    const [fileSystem, setFileSystem] = useState({});

    // const [cookies, setCookie, removeCookie] = useCookies('token');
    // var jwtDecode = require('jwt-decode');
    // var jwt = require("jsonwebtoken");
    // var decoded = jwt.decode(cookies);

    const contextValue = {
        fileList,
        setFileList,
        fileSystem,
        setFileSystem
    }

    const loginInfo = jwt_decode(cookie.load('token'));

    const refreshCallback = () => {
        let params = {
            userId : loginInfo['userId'],
            repoId : loginInfo['repoId']
        }
        // console.log(params)
        axios
            .get(BASE_URL + "/history", {params})
            .then((res) => {
                if(res.status === 200){
                    // console.log("res",res);
                    // console.log(fileList);
                    setFileList(res.data);
                    // console.log(fileList);
                }
            })
            .catch((error) => {
                console.log(error)
            });
    }

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
                creatorName: loginInfo["name"],
                parentID: '1382b6993e9f270cb1c29833be3f5750',
                parentPath: '/',
                path: '/' + file.fileName
            }
        });

        console.log(fileSystem);
        localStorage.setItem('fileSystem', JSON.stringify(fileSystem));
        return fileSystem;
    }

    return(
        <FileListContext.Provider value={contextValue}>
            <ContainerLeft>
                {/*{console.log(cookies)}*/}
                {/*/!*{decoded}*!/*/}
                <UploadNew refreshCallback = {refreshCallback} />
                <MyControl refreshCallback = {refreshCallback} />
            </ContainerLeft>

            <ContainerRight>
                <History refreshCallback = {refreshCallback} />
            </ContainerRight>
        </FileListContext.Provider>
    );
}