import React from 'react';
import { hydrateRoot } from 'react-dom/client';
import { App } from './App';

const { path, search } = (window as any).__INITIAL_DATA__ || {
    path: window.location.pathname,
    search: window.location.search,
};

hydrateRoot(document.getElementById('root')!, <App initialPath={path} initialSearch={search} />);