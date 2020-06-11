import React from "react";

const defaultContext = {
    markdownText: "",
    setMarkdownText: () => {},
    titleText: "",
    setTitleText: () => {}
};

export default React.createContext(defaultContext);
