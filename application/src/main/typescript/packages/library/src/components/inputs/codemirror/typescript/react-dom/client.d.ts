/**
 * WARNING: This entrypoint is only available starting with `react-dom@18.0.0-rc.1`
 */

// See https://github.com/facebook/react/blob/main/packages/react-dom/client.js to see how the exports are declared,

import React = require("react");

export {};

declare const REACT_FORM_STATE_SIGIL: unique symbol;
export interface ReactFormState {
    [REACT_FORM_STATE_SIGIL]: never;
}

export interface HydrationOptions {
    formState?: ReactFormState | null;
    /**
     * Prefix for `useId`.
     */
    identifierPrefix?: string;
    onUncaughtError?:
        | ((error: unknown, errorInfo: { componentStack?: string | undefined }) => void)
        | undefined;
    onRecoverableError?: (error: unknown, errorInfo: ErrorInfo) => void;
    onCaughtError?:
        | ((
        error: unknown,
        errorInfo: {
            componentStack?: string | undefined;
            errorBoundary?: React.Component<unknown> | undefined;
        },
    ) => void)
        | undefined;
}

export interface RootOptions {
    /**
     * Prefix for `useId`.
     */
    identifierPrefix?: string;
    onUncaughtError?:
        | ((error: unknown, errorInfo: { componentStack?: string | undefined }) => void)
        | undefined;
    onRecoverableError?: (error: unknown, errorInfo: ErrorInfo) => void;
    onCaughtError?:
        | ((
        error: unknown,
        errorInfo: {
            componentStack?: string | undefined;
            errorBoundary?: React.Component<unknown> | undefined;
        },
    ) => void)
        | undefined;
}

export interface ErrorInfo {
    componentStack?: string;
}

export interface Root {
    render(children: React.ReactNode): void;
    unmount(): void;
}

/**
 * Different release channels declare additional types of ReactNode this particular release channel accepts.
 * App or library types should never augment this interface.
 */
// eslint-disable-next-line @typescript-eslint/no-empty-interface
export interface DO_NOT_USE_OR_YOU_WILL_BE_FIRED_EXPERIMENTAL_CREATE_ROOT_CONTAINERS {}

export type Container =
    | Element
    | DocumentFragment
    | Document
    | DO_NOT_USE_OR_YOU_WILL_BE_FIRED_EXPERIMENTAL_CREATE_ROOT_CONTAINERS[
    keyof DO_NOT_USE_OR_YOU_WILL_BE_FIRED_EXPERIMENTAL_CREATE_ROOT_CONTAINERS
    ];

/**
 * createRoot lets you create a root to display React components inside a browser DOM node.
 *
 * @see {@link https://react.dev/reference/react-dom/client/createRoot API Reference for `createRoot`}
 */
export function createRoot(container: Container, options?: RootOptions): Root;

/**
 * Same as `createRoot()`, but is used to hydrate a container whose HTML contents were rendered by ReactDOMServer.
 *
 * React will attempt to attach event listeners to the existing markup.
 *
 * **Example Usage**
 *
 * ```jsx
 * hydrateRoot(document.querySelector('#root'), <App />)
 * ```
 *
 * @see https://reactjs.org/docs/react-dom-client.html#hydrateroot
 */
export function hydrateRoot(
    container: Element | Document,
    initialChildren: React.ReactNode,
    options?: HydrationOptions,
): Root;
