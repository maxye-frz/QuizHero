import 'rc-dialog/assets/index.css';
import * as React from 'react';
import * as ReactDOM from 'react-dom';
import Dialog from 'rc-dialog';
import {Button, Select, message} from "antd";
import axios from "axios"
import {BASE_URL} from "../../config/config";
import jwt_decode from "jwt-decode";
import cookie from "react-cookies";
import {GithubOutlined, PullRequestOutlined, RollbackOutlined} from "@ant-design/icons";

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
            repoList: {},
            fileList: {},
            visible: false,
            visible2: false,
            width: 600,
            destroyOnClose: false,
            center: false,
            mousePosition: undefined,
            useIcon: false,
            forceRender: false,
            owner: '',
            repo: '',
            path: '/',
            branch: '',
        };

        this.handleSubmit = this.handleSubmit.bind(this);
        this.selectRepo = this.selectRepo.bind(this);
        // this.selectBranch = this.selectBranch.bind(this);
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

        axios
            .get(BASE_URL + '/listRepo')
            .then(res => {this.setState({
                repoList: res.data,
                path: '/'
            });
                console.log(res.data);
            })
            .catch(error => console.log(error))
    }

    onClose = (e: React.SyntheticEvent) => {
        this.setState({
            visible: false,
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


    selectRepo (value) {
        const owner_repo = value.split('/');
        console.log(owner_repo, owner_repo[0], owner_repo[1]);
        this.setState({
            owner: owner_repo[0],
            repo: owner_repo[1],
            path: '/'
        })

        this.refreshFileOptions(owner_repo[0], owner_repo[1], '/');
    }

    // selectBranch (e) {
    //     this.setState({
    //         branch: e.target.value
    //     })
    // }

    selectFile (value) {
        this.refreshFileOptions(this.state.owner, this.state.repo, value);
        this.setState({
            path: value
        })

    }

    back = () => {
        const currPath = this.state.path;
        const currPathArray = currPath.split('/');
        currPathArray.splice(currPathArray.length - 1, 1)
        console.log(currPathArray.join('/'));

        this.refreshFileOptions(this.state.owner, this.state.repo, currPathArray.join('/'));
        this.setState({
            path: currPathArray.join('/') === '' ? '/' : currPathArray.join('/')
        })
    }

    refreshFileOptions (owner, repo, path) {
        console.log(this.state.owner, this.state.repo, this.state.path);
        console.log(owner, repo, path);
        let params = {
            owner: owner,
            repo: repo,
            path: path
        }

        axios
            .get(BASE_URL + '/listContent', {params})
            .then(res => {
                console.log(res.data);
                this.setState({
                    fileList: res.data
                });
            })
            .catch(error => console.log(error))
    }

    handleSubmit (e) {
        // handle edge case need to select md file
        if (this.state.owner === '' || this.state.repo === '' || this.state.path === '/') {
            alert('Please select file')
            e.preventDefault(); // 阻止默认行为，在提交之前需要验证的时候先拦截一下
        } else {
            // alert('You pulled ' + this.state.owner + ' ' + this.state.repo + ' ' + this.state.path);
            let params = {
                owner: this.state.owner,
                repo: this.state.repo,
                path: this.state.path
            }

            const loginInfo = jwt_decode(cookie.load('token'));
            console.log(loginInfo);
            const formData = new FormData();
            formData.append('userId', loginInfo['userId']);
            formData.append('repoId', loginInfo['repoId']);

            axios
                .post(BASE_URL + '/clone', formData, {params})
                .then(res => {
                    // const fileId = res.data.fileId;
                    // localStorage.setItem("newFileName", res.data.fileName);
                    // localStorage.setItem("newFileString", res.data.rawString);
                    console.log(res.data);
                    this.props.refreshCallback();
                    this.onClose();
                    // window.open("/EditPage", "_self");
                })
                .catch(error => console.log(error))
        }
    }

    render() {
        const { repoList } = this.state;

        let repoOptions = repoList.length > 0
            && repoList.map((item, i) => {
                return (
                    <Select.Option key={i} value={item}>{item}</Select.Option>
                )
            }, this);

        const { fileList } = this.state;

        let fileOptions = fileList.length > 0
            && fileList.map((item, i) => {
                return (
                    <Select.Option key={i} value={item}>{item}</Select.Option>
                )
            }, this);

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
                closeIcon={this.state.useIcon ? getSvg(clearPath, {}, true) : undefined}
                focusTriggerAfterClose={false}
            >
                <form className="ant-form-horizontal"
                      onSubmit={this.handleSubmit}>
                    <div style={{ height: 20 }} />
                    <div className="ant-form-item">
                        <label className="col-6" required>Select repo：</label>
                        <div style={{ height: 10 }} />
                        <div className="col-14">
                            <Select className="control-input"
                                    defaultValue="Please select" showSearch={true}
                                    // searchPlaceholder="Input"
                                    onChange={this.selectRepo}>
                                {/*<Select.Option value="no select">Please select</Select.Option>*/}
                                {repoOptions}
                            </Select>
                        </div>
                    </div>
                    {/*<div>*/}
                    {/*    <label>Select branch：</label>*/}
                    {/*    <select onChange={this.selectBranch}>*/}
                    {/*        /!*<option value="no select">Please select</option>*!/*/}
                    {/*        <option value="master">master</option>*/}
                    {/*    </select>*/}
                    {/*</div>*/}
                    <div style={{ height: 30 }} />
                    <div>
                        <label>Select file：</label>
                        <div style={{ height: 10 }} />
                        <Select defaultValue={this.state.path} showSearch={true}
                            // searchPlaceholder="Input"
                            onChange={this.selectFile}>
                            <Select.Option value={this.state.path}>{this.state.path}</Select.Option>
                            {fileOptions}
                        </Select>

                    </div>

                    <div style={{ height: 50 }} />
                    {/*<div className="row">*/}
                    {/*    <div className="col-16 col-offset-6">*/}
                    {/*        <input type="submit" className="ant-btn ant-btn-primary" value="Pull" />*/}
                    {/*    </div>*/}
                    {/*</div>*/}
                </form>

                <Button
                    className="back-button"
                    onClick={this.back}>
                    <RollbackOutlined /> Back
                </Button>

                <Button
                    className="pull-button"
                    onClick={this.handleSubmit}>
                    <PullRequestOutlined />Clone
                </Button>

                {/*<button onClick={this.onClose}>close</button>*/}
                {/*<button onClick={this.changeWidth}>change width</button>*/}
                {/*<button onClick={this.toggleCloseIcon}>*/}
                {/*    use custom icon, is using icon: {this.state.useIcon && 'true' || 'false'}.*/}
                {/*</button>*/}
            </Dialog>
        );

        return (
            <div>
                <Button
                    className='clone-button'
                    onClick={this.onClick}>
                    <GithubOutlined /> Clone from GitHub
                </Button>
                {dialog}
            </div>

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