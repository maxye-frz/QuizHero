import 'rc-dialog/assets/index.css';
import * as React from 'react';
import * as ReactDOM from 'react-dom';
import Dialog from 'rc-dialog';
import {Button, message} from "antd";

const clearPath = 'M793 242H366v-74c0-6.7-7.7-10.4-12.9' +
    '-6.3l-142 112c-4.1 3.2-4.1 9.4 0 12.6l142 112c' +
    '5.2 4.1 12.9 0.4 12.9-6.3v-74h415v470H175c-4.4' +
    ' 0-8 3.6-8 8v60c0 4.4 3.6 8 8 8h618c35.3 0 64-' +
    '28.7 64-64V306c0-35.3-28.7-64-64-64z';

const getSvg = (path: string, props = {}, align = false) => {
    return (
        <i {...props}>
            <svg
                viewBox="0 0 1024 1024"
                width="1em"
                height="1em"
                fill="currentColor"
                style={align ? { verticalAlign: '-.125em ' } : {}}
            >
                <path d={path} p-id="5827"></path>
            </svg>
        </i>
    );
};

class MyControl extends React.Component<any, any> {
    constructor () {
        super();
        this.state = {
            visible: false,
            visible2: false,
            width: 600,
            destroyOnClose: false,
            center: false,
            mousePosition: undefined,
            useIcon: false,
            forceRender: false,
            repo: '',
            branch: '',
            file: ''
        };

        this.handleSubmit = this.handleSubmit.bind(this);
        this.selectRepo = this.selectRepo.bind(this);
        this.selectBranch = this.selectBranch.bind(this);
        this.selectFile = this.selectFile.bind(this);
    }

    onClick = (e: React.MouseEvent) => {
        this.setState({
            mousePosition: {
                x: e.pageX,
                y: e.pageY,
            },
            visible: true,
        });
    }

    onClose = (e: React.SyntheticEvent) => {
        this.setState({
            visible: false,
        });
    }

    onDestroyOnCloseChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        this.setState({
            destroyOnClose: e.target.checked,
        });
    }

    onForceRenderChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        this.setState({
            forceRender: e.target.checked,
        });
    }

    changeWidth = () => {
        this.setState({
            width: this.state.width === 600 ? 800 : 600,
        });
    }

    center = (e: React.ChangeEvent<HTMLInputElement>) => {
        this.setState({
            center: e.target.checked,
        });
    }

    toggleCloseIcon = () => {
        this.setState({
            useIcon: !this.state.useIcon,
        });
    }


    selectRepo (e) {
        this.setState({
            repo: e.target.value
        })
    }

    selectBranch (e) {
        this.setState({
            branch: e.target.value
        })
    }

    selectFile (e) {
        this.setState({
            file: e.target.value
        })
    }

    handleSubmit (e) {
        if (this.state.repo === '' || this.state.branch === '' || this.state.file === '') {
            alert('Please select file')
            e.preventDefault(); // 阻止默认行为，在提交之前需要验证的时候先拦截一下
        } else {
            alert('You pulled ' + this.state.repo + ' ' + this.state.branch + ' ' + this.state.file);
        }
    }

    render() {
        const style = {
            width: this.state.width,
        };
        let wrapClassName = '';
        if (this.state.center) {
            wrapClassName = 'center';
        }
        const dialog = (
            <Dialog
                visible={this.state.visible}
                wrapClassName={wrapClassName}
                animation="zoom"
                maskAnimation="fade"
                onClose={this.onClose}
                style={style}
                mousePosition={this.state.mousePosition}
                destroyOnClose={this.state.destroyOnClose}
                closeIcon={this.state.useIcon ? getSvg(clearPath, {}, true) : undefined}
                forceRender={this.state.forceRender}
                focusTriggerAfterClose={false}
            >
                <form onSubmit={this.handleSubmit}>
                    <div>
                        <label>Select repo：</label>
                        <select onChange={this.selectRepo}>
                            <option value="no select">Please select</option>
                            <option value="QuizHero">QuizHero</option>
                        </select>
                    </div>
                    <div>
                        <label>Select branch：</label>
                        <select onChange={this.selectBranch}>
                            <option value="no select">Please select</option>
                            <option value="master">master</option>
                            <option value="develop">develop</option>
                        </select>
                    </div>
                    <div>
                        <label>Select file：</label>
                        <select onChange={this.selectFile}>
                            <option value="no select">Please select</option>
                            <option value="template.md">template.md</option>
                            <option value="README.md">README.md</option>
                        </select>
                    </div>

                    <div style={{ height: 200 }} />
                    <input type="submit" value="Pull" />
                </form>

                {/*<input autoFocus />*/}
                {/*<p>basic modal</p>*/}
                {/*<button onClick={this.onClose}>关闭当前的</button>*/}
                {/*<button onClick={this.changeWidth}>change width</button>*/}
                {/*<button onClick={this.toggleCloseIcon}>*/}
                {/*    use custom icon, is using icon: {this.state.useIcon && 'true' || 'false'}.*/}
                {/*</button>*/}
            </Dialog>
        );

        return (
            <p>
                <Button onClick={this.onClick}>
                    Pull from GitHub
                </Button>
                {dialog}
            </p>

            // <div style={{ width: '90%', margin: '0 auto' }}>
            //     <style>
            //         {`
            // .center {
            //   display: flex;
            //   align-items: center;
            //   justify-content: center;
            // }
            // `}
            //     </style>
            //     <p>
            //         <button className="btn btn-primary" onClick={this.onClick}>
            //             show dialog
            //         </button>
            //         &nbsp;
            //         <label>
            //             destroy on close:
            //             <input
            //                 type="checkbox"
            //                 checked={this.state.destroyOnClose}
            //                 onChange={this.onDestroyOnCloseChange}
            //             />
            //         </label>
            //         &nbsp;
            //         <label>
            //             center
            //             <input
            //                 type="checkbox"
            //                 checked={this.state.center}
            //                 onChange={this.center}
            //             />
            //         </label>
            //         &nbsp;
            //         <label>
            //             force render
            //             <input
            //                 type="checkbox"
            //                 checked={this.state.forceRender}
            //                 onChange={this.onForceRenderChange}
            //             />
            //         </label>
            //     </p>
            //     {dialog}
            // </div>
        );
    }
}

export default MyControl;

// ReactDOM.render(
//     <div>
//         <h2>ant-design dialog</h2>
//         <MyControl />
//     </div>,
//     document.getElementById('__react-content'),
// );