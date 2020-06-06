import React from "react";

const defaultContext = {
    titleText: "",
    setTitleText: () => {}
};

export default React.createContext(defaultContext);
