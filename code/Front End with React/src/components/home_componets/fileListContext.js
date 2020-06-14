import React from "react";

const defaultContext = {
    fileList: [],
    setFileList: () => {}
}

export default React.createContext(defaultContext);