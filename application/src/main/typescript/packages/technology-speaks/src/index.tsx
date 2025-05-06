import './index.css';
import React, {StrictMode} from 'react';
import ReactDOM from 'react-dom/client';
import {System} from "react-ui-simplicity";
import {routes} from "./routes";
import {init} from "./Persistence"

init()

const root = ReactDOM.createRoot(document.getElementById('root'))

root.render(
    <System routes={routes}/>
);

