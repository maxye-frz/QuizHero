import React from "react";

const defaultContext = {
    titleText: "",
    setTitleText: () => {},
    markdownText: "",
    setMarkdownText: () => {},
    CSS: "",
    setCSS: () => {}
};

export default React.createContext(defaultContext);
