import realtimeTheme from "./realtime-theme"

const marpit2realtime = (marpitCSS) => {
    console.log(marpitCSS);
    if (marpitCSS === "") return realtimeTheme;
    let sections = marpitCSS.split("}");
    console.log(sections);

    let section = sections[0], h1 = sections[1], h2 = sections[2];
    const fontSize = section.split("font-size:")[1].split("px")[0];
    const padding = section.split("padding:")[1].split("px")[0];
    section += `  font-size: ${parseInt(fontSize)/28.5}vw;
      padding: ${parseInt(padding)/25.6}vw;
      height: calc(28.125vw);
      width: calc(50vw);
      border-bottom: 1px solid rgba(15, 15, 15, 0.3);
      border-top: 1px solid rgba(15, 15, 15, 0.3);
    }`

    h1 += `  font-size: 2.5vw;
    margin: 1.4vw;
    }`

    h2 += `  font-size: 2vw;
    margin: 1.3vw;
    }`

    const realtimeCSS = section + h1 + h2 + `
    
    p {
      margin: 1.2vw;
      line-height: 1.5;
    }`
    console.log(realtimeCSS);
    return realtimeCSS;
}

export default marpit2realtime;

    // `
    // /* @theme example */
    //
    // section {
    //   background-color: #FFFAFA;
    //   color: #000;
    //   font-size: 30px;
    //   padding: 40px;
    // }
    //
    // h1 {
    //   color: #FF1493;
    //   text-align: center;
    // }
    //
    // h2 {
    //   color: #FF1493;
    //   text-align: center;
    //   margin: 0;
    // }
    // `
    //     `
    // /* @theme example */
    //
    // section {
    //   background-color: #FFFAFA;
    //   height: calc(28vw);
    //   width: calc(50vw);
    //   color: #000;
    //   font-size: 1vw;
    //   padding: 1.4vw;
    //   border-bottom: 1px solid rgba(15, 15, 15, 0.3);
    //   border-top: 1px solid rgba(15, 15, 15, 0.3);
    // }
    //
    // h1 {
    //   font-size: 2.5vw;
    //   color: #FF1493;
    //   text-align: center;
    //   margin: 1.4vw;
    // }
    //
    // h2 {
    //   font-size: 2vw;
    //   color: #FF1493;
    //   text-align: center;
    //   margin: 1.3vw;
    // }
    //
    // p {
    //   margin: 1.2vw;
    //   line-height: 1.5;
    // }
    // `