import UploadNew from "./UploadNew";
import History from "./History";
import FileListContext from "./fileListContext";
import React, {useState} from "react";
// import {useCookies} from "react-cookie";
// import jwt from "jwt-decode";
import styled from "styled-components";
import axios from "axios";
import {BASE_URL} from "../../config/config";

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

    // const [cookies, setCookie, removeCookie] = useCookies('token');
    // var jwtDecode = require('jwt-decode');
    // var jwt = require("jsonwebtoken");
    // var decoded = jwt.decode(cookies);

    const contextValue = {
        fileList,
        setFileList
    }

    const refreshCallback = () => {
        let params = {
            userId : localStorage.getItem("instructorId")
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

    return(
        <FileListContext.Provider value={contextValue}>
            <ContainerLeft>
                {/*{console.log(cookies)}*/}
                {/*/!*{decoded}*!/*/}
                <UploadNew refreshCallback = {refreshCallback} />
            </ContainerLeft>

            <ContainerRight>
                <History refreshCallback = {refreshCallback} />
            </ContainerRight>
        </FileListContext.Provider>
    );
}