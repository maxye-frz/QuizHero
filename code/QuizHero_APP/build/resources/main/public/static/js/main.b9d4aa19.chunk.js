(this["webpackJsonpmy-app"]=this["webpackJsonpmy-app"]||[]).push([[0],{1041:function(e,t,n){"use strict";n.r(t);var a=n(1),o=n.n(a),r=n(12),s=n.n(r),i=(n(429),n(60)),u=n(61),l=n(65),c=n(62),d=n(66),h=n(406),m=n.n(h),f=(n(430),n(1045)),p=n(1042),w=n(1043),v=n(31),g=n(407),b={name:"file",action:"https://www.mocky.io/v2/5cc8019d300000980a055e76",headers:{authorization:"authorization-text"}},y=new(n.n(g).a);y.themeSet.default=y.themeSet.add("\n            /* @theme example */\n\n            section {\n              background-color: #369;\n              color: #fff;\n              font-size: 30px;\n              padding: 40px;\n            }\n\n        h1,\n        h2 {\n          text-align: center;\n          margin: 0;\n        }\n\n        h1 {\n          color: #8cf;\n        }\n        ");var q=function(e){function t(e){var n;return Object(i.a)(this,t),(n=Object(l.a)(this,Object(c.a)(t).call(this,e))).state={file:"",result:"",rawString:"",quiz:[]},n.beforeUpload=function(e){console.log("FILEEE",e),n.setState({file:e})},n.onChange=function(e){"uploading"!==e.file.status&&console.log(e.file,e.fileList),"done"===e.file.status?(console.log(e.file.name),f.a.success("".concat(e.file.name," file uploaded successfully")),n.readFile(n.state.file).then(n.convertText)):"error"===e.file.status&&(console.log(e.file.name),f.a.error("".concat(e.file.name," file upload failed.")))},n.onDownload=function(e){!function(e,t){var n=window.URL||window.webkitURL||window,a=new Blob([t]),o=document.createElementNS("http://www.w3.org/1999/xhtml","a");o.href=n.createObjectURL(a),o.download=e,function(e){var t=document.createEvent("MouseEvents");t.initMouseEvent("click",!0,!1,window,0,0,0,0,0,!1,!1,!1,!1,0,null),e.dispatchEvent(t)}(o)}("filename.html",n.state.result),console.log(n.state.rawString)},n.onPreview=function(e){n.trans()},n.readFile=function(e){return new Promise((function(t,n){var a=new FileReader;console.log("1"),a.readAsText(e),console.log("2"),a.onload=function(e){t(a.result),console.log("3")},a.onerror=function(e){n(e)}}))},n.convertText=function(e){n.setState({rawString:e});var t=y.render(e),a=t.html,o=t.css,r="\n            <!DOCTYPE html>\n            <html><body>\n              <style>".concat(o,"</style>\n              ").concat(a,"\n            </body></html>\n            ");n.setState({result:r})},n.parseString=function(e){for(var t=new Array,n=e.split("\n\n"),a=0;a<n.length;a++){for(var o="A",r={question:"",answers:[]},s=n[a].split("\n"),i=0;i<s.length;i++){var u=s[i].split(" ");if(u.length>1)if(console.log(u),"Question:"===u[1]){var l=u.slice(2,u.length);r.question=l.join(" ")}else if("*"===u[1].charAt(0)){r.answers.push({type:o,content:u[1].substring(2,u[1].length-2)});var c=o.charCodeAt(0);o=String.fromCharCode(c+1)}else{r.answers.push({type:o,content:u[1]});c=o.charCodeAt(0);o=String.fromCharCode(c+1)}}t.push(r)}return t},n.trans=function(){var e=n.parseString(n.state.rawString);console.log(e),n.setState({quiz:e}),n.props.callback(e)},n.callback=e.callback,n}return Object(d.a)(t,e),Object(u.a)(t,[{key:"render",value:function(){return o.a.createElement("div",null,o.a.createElement(p.a,Object.assign({onChange:this.onChange,beforeUpload:this.beforeUpload,onDownload:this.onDownload,onPreview:this.onPreview},b),o.a.createElement(w.a,null,o.a.createElement(v.a,{type:"upload"})," Click to Upload")))}}]),t}(o.a.Component),E=n(165),S=n(419),C=n(164),k=n(166);var z=function(e){return o.a.createElement("h2",{className:"question"},e.content)};var O=function(e){return o.a.createElement("div",{className:"questionCount"},"Question ",o.a.createElement("span",null,e.counter)," of ",o.a.createElement("span",null,e.total))};var A=function(e){return o.a.createElement("li",{className:"answerOption"},o.a.createElement("input",{type:"radio",className:"radioCustomButton",name:"radioGroup",checked:e.answerType===e.answer,id:e.answerType,value:e.answerType+" "+e.questionId+" "+e.answerContent,disabled:e.answer,onChange:e.onAnswerSelected}),o.a.createElement("label",{className:"radioCustomLabel",htmlFor:e.answerType},e.answerContent))};var j=function(e){return o.a.createElement(k.CSSTransitionGroup,{className:"container",component:"div",transitionName:"fade",transitionEnterTimeout:800,transitionLeaveTimeout:500,transitionAppear:!0,transitionAppearTimeout:500},o.a.createElement("div",{key:e.questionId},o.a.createElement(O,{counter:e.questionId,total:e.questionTotal}),o.a.createElement(z,{content:e.question}),o.a.createElement("ul",{className:"answerOptions"},e.answerOptions.map((function(t){return o.a.createElement(A,{key:t.content,answerContent:t.content,answerType:t.type,answer:e.answer,questionId:e.questionId,onAnswerSelected:e.onAnswerSelected})})))))},x=n(420),T=n.n(x),I=n(167),N=n.n(I),Q=function(e){function t(e){var n;return Object(i.a)(this,t),(n=Object(l.a)(this,Object(c.a)(t).call(this,e))).state={quizData:[]},n}return Object(d.a)(t,e),Object(u.a)(t,[{key:"componentDidMount",value:function(){var e=this,t=document.location.origin;N.a.get(t+"/quizstat",{params:{fileId:1,questionId:1}}).then((function(t){200===t.status&&(e.setState({quizData:t.data}),console.log("res",t))})).catch((function(e){console.log("error")}))}},{key:"render",value:function(){var e=this;console.log("quiz data",typeof this.state.quizData);for(var t=this.state.quizData,n=0;n<t.length;n++)t[n].answer="B";var a=0,r=[],s=[],i=[],u=[],l=[],c=[];for(var d in t)a+=1,r.push("Q"+a),s.push(t[d].countA),i.push(t[d].countB),u.push(t[d].countC),l.push(t[d].countD),c.push(t[d].answer);var h={title:{text:"Quiz Statistic",x:"center"},tooltip:{trigger:"axis",axisPointer:{type:"shadow"}},legend:{data:["A","B","C","D"],x:"left"},xAxis:{name:"Quiz Number",data:r},yAxis:{name:"Amount of Choices"},series:[{name:"A",type:"bar",barGap:.1,barWidth:30,data:s,itemStyle:{normal:{color:function(e){return"A"==c[e.dataIndex]?"#FE8463":"#C6E579"}}}},{name:"B",type:"bar",barWidth:30,data:i,itemStyle:{normal:{color:function(e){return"B"==c[e.dataIndex]?"#FE8463":"#C6E579"}}}},{name:"C",type:"bar",barWidth:30,data:u,itemStyle:{normal:{color:function(e){return"C"==c[e.dataIndex]?"#FE8463":"#C6E579"}}}},{name:"D",type:"bar",barWidth:30,data:l,itemStyle:{normal:{color:function(e){return"D"==c[e.dataIndex]?"#FE8463":"#C6E579"}}}}]};return o.a.createElement(T.a,{ref:function(t){e.echartsElement=t},option:h,theme:"clear"})}}]),t}(o.a.Component);var D=function(e){return o.a.createElement("div",null,o.a.createElement(k.CSSTransitionGroup,{className:"container result",component:"div",transitionName:"fade",transitionEnterTimeout:800,transitionLeaveTimeout:500,transitionAppear:!0,transitionAppearTimeout:500},o.a.createElement("div",null,"Thank you for finishing the quiz, here is the statistics:")),o.a.createElement(Q,null))},F=function(e){function t(e){var n;return Object(i.a)(this,t),(n=Object(l.a)(this,Object(c.a)(t).call(this,e))).state={upload:0,counter:0,questionId:1,question:"",answerOptions:[],answer:"",answersCount:{},result:"",quizQuestions:e.questions},n.handleAnswerSelected=n.handleAnswerSelected.bind(Object(C.a)(n)),n}return Object(d.a)(t,e),Object(u.a)(t,[{key:"componentDidMount",value:function(){var e=this,t=this.state.quizQuestions.map((function(t){return e.shuffleArray(t.answers)}));this.setState({question:this.state.quizQuestions[0].question,answerOptions:t[0]})}},{key:"shuffleArray",value:function(e){for(var t,n,a=e.length;0!==a;)n=Math.floor(Math.random()*a),t=e[a-=1],e[a]=e[n],e[n]=t;return e}},{key:"handleAnswerSelected",value:function(e){var t=this;this.setUserAnswer(e.currentTarget.value),console.log(e.currentTarget.value),this.state.questionId<this.state.quizQuestions.length?setTimeout((function(){return t.setNextQuestion()}),300):setTimeout((function(){return t.setResults(t.getResults())}),300)}},{key:"setUserAnswer",value:function(e){var t=e.split(" "),n=t[0],a=t[1];t[2];console.log(t),this.setState((function(t,a){return{answersCount:Object(S.a)({},t.answersCount,Object(E.a)({},e,(t.answersCount[n]||0)+1)),answer:n}}));var o=document.location.origin,r={fileId:1,questionId:parseInt(a),choice:n};console.log(r),N.a.post(o+"/record",r,{headers:{"Content-Type":"multipart/form-data"}}).then((function(){console.log("upload success")})).catch((function(e){console.log("error")}))}},{key:"setNextQuestion",value:function(){var e=this.state.counter+1,t=this.state.questionId+1;this.setState({counter:e,questionId:t,question:this.state.quizQuestions[e].question,answerOptions:this.state.quizQuestions[e].answers,answer:""})}},{key:"getResults",value:function(){var e=this.state.answersCount,t=Object.keys(e),n=t.map((function(t){return e[t]})),a=Math.max.apply(null,n);return t.filter((function(t){return e[t]===a}))}},{key:"setResults",value:function(e){1===e.length?this.setState({result:e[0]}):this.setState({result:"Undetermined"})}},{key:"renderQuiz",value:function(){return o.a.createElement(j,{answer:this.state.answer,answerOptions:this.state.answerOptions,questionId:this.state.questionId,question:this.state.question,questionTotal:this.state.quizQuestions.length,onAnswerSelected:this.handleAnswerSelected})}},{key:"renderResult",value:function(){return o.a.createElement(D,{quizResult:this.state.result})}},{key:"render",value:function(){return o.a.createElement("div",{className:"App"},o.a.createElement("div",{className:"Quiz-header"}),this.state.result?this.renderResult():this.renderQuiz())}}]),t}(a.Component),U=function(e){function t(e){var n;return Object(i.a)(this,t),(n=Object(l.a)(this,Object(c.a)(t).call(this,e))).callback=function(e){n.setState({quiz:e}),n.setState({quizFlag:1})},n.statisticButtonClicked=function(){window.location="/quizStatistic"},n.renderUploadPage=function(){return o.a.createElement("div",{className:"App"},o.a.createElement("header",{className:"App-header"},o.a.createElement("img",{src:m.a,className:"App-logo",alt:"logo"}),o.a.createElement(q,{callback:n.callback})))},n.state={quiz:"",quizFlag:0},n}return Object(d.a)(t,e),Object(u.a)(t,[{key:"renderQuizPage",value:function(){return console.log(this.state.quiz),o.a.createElement(F,{questions:this.state.quiz})}},{key:"render",value:function(){return o.a.createElement("div",null,this.state.quizFlag?this.renderQuizPage():this.renderUploadPage())}}]),t}(a.Component);Boolean("localhost"===window.location.hostname||"[::1]"===window.location.hostname||window.location.hostname.match(/^127(?:\.(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)){3}$/));s.a.render(o.a.createElement(U,null),document.getElementById("root")),"serviceWorker"in navigator&&navigator.serviceWorker.ready.then((function(e){e.unregister()})).catch((function(e){console.error(e.message)}))},406:function(e,t,n){e.exports=n.p+"static/media/logo.f45975d0.png"},424:function(e,t,n){e.exports=n(1041)},429:function(e,t,n){},430:function(e,t,n){},492:function(e,t){},493:function(e,t){},494:function(e,t){},512:function(e,t){}},[[424,1,2]]]);
//# sourceMappingURL=main.b9d4aa19.chunk.js.map