import React from 'react';
import ReactDom from 'react-dom';
import Popup from "reactjs-popup";

class Select extends React.Component {
    constructor () {
        super();
        this.state = {
            repo: '',
            branch: '',
            file: ''
        }
        this.handleSubmit = this.handleSubmit.bind(this);
        this.selectRepo = this.selectRepo.bind(this);
        this.selectBranch = this.selectBranch.bind(this);
        this.selectFile = this.selectFile.bind(this);
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
        } else {
            alert('You pulled ' + this.state.repo + ' ' + this.state.branch + ' ' + this.state.file);
            e.preventDefault(); // 阻止默认行为，在提交之前需要验证的时候先拦截一下
        }
    }

    render () {
        return (
            <div>
                <form onSubmit={this.handleSubmit}>
                    <label>Select repo：</label>
                    <select onChange={this.selectRepo}>
                        <option value="no select">Please select</option>
                        <option value="apple">apple</option>
                        <option value="banana">banana</option>
                        <option value="pear">pear</option>
                        <option value="orange">orange</option>
                    </select>
                    <label>Select branch：</label>
                    <select onChange={this.selectBranch}>
                        <option value="no select">Please select</option>
                        <option value="master">master</option>
                        <option value="develop">develop</option>
                    </select>
                    <label>Select file：</label>
                    <select onChange={this.selectFile}>
                        <option value="no select">Please select</option>
                        <option value="template.md">template.md</option>
                        <option value="README.md">README.md</option>
                    </select>
                    <input type="submit" value="Pull" />
                </form>
            </div>
        )
    }
}

// export default Select;

// ReactDom.render(
//     <div>
//         <Select />
//     </div>,
//     document.getElementById('root')
// )

export default () => (
    <Popup trigger={<button> Trigger</button>} position="right center">
        <div>Popup content here !!</div>
    </Popup>
);

