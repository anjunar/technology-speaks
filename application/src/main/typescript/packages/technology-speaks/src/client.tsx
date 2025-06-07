import React from 'react';
import {hydrateRoot} from 'react-dom/client';
import {App} from './App';

hydrateRoot(document.getElementById('root')!, (
    <App initialPath={window.location.pathname}
         initialSearch={window.location.search}
         initialData={<div></div>}
    />
));