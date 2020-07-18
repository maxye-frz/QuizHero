import spectacleTheme from "./spectacle-theme";

const marpit2spectacle = (marpitCSS) => {
    console.log(marpitCSS);
    if (marpitCSS === "") return spectacleTheme;
    let sections = marpitCSS.split("}");
    console.log(sections);

    let section = sections[0], h1 = sections[1], h2 = sections[2];
    const primary = section.split(" color:")[1].split(";")[0];
    console.log(primary);
    const tertiary = section.split("background-color:")[1].split(";")[0];
    console.log(tertiary);
    const quaternary = '#00f';

    return {
        colors: {
            primary: primary,
            tertiary: tertiary,
            quaternary: quaternary
        },
        fonts: {
            header: '"Helvetica Neue", Helvetica, Arial, sans-serif'
        },
        fontSizes: {
            h1: '60px',
            h2: '40px',
            text:'24px',
            header: '64px',
            paragraph: '28px'
        }
    }
}

export default marpit2spectacle;
    // `
    // /* @theme example */
    //
    // section {
    //   background-color: #F5FBFF;
    //   color: #000;
    //   font-size: 30px;
    //   padding: 40px;
    // }
    //
    // h1 {
    //   color: #000;
    //   text-align: center;
    // }
    //
    // h2 {
    //   color: #000;
    //   text-align: center;
    //   margin: 0;
    // }
    // `
// export default {
//     colors: {
//         primary: '#000', // paragraph color
//         secondary: '#FF1493', // header color
//         tertiary: '#FFFAFA', // background color
//         quaternary: '#00f' // hyperlink color
//     },
//     fonts: {
//         header: '"Helvetica Neue", Helvetica, Arial, sans-serif'
//     },
//     fontSizes: {
//         h1: '60px',
//         h2: '40px',
//         text:'24px',
//         header: '64px',
//         paragraph: '28px'
//     }
// };