import {mapForm, Router} from "react-ui-simplicity";
import App from "./App";
import FormPage from "./pages/navigator/FormPage";
import TablePage from "./pages/navigator/TablePage";
import {process} from "./App"
import HomePage from "./pages/home/HomePage";
import LoginPage from "./pages/security/LoginPage";
import RegisterPage from "./pages/security/RegisterPage";
import ConfirmationPage from "./pages/security/ConfirmationPage";
import {UAParser} from "ua-parser-js";
import WebAuthnLogin from "./domain/security/WebAuthnLogin";
import LogoutPage from "./pages/security/LogoutPage";
import SearchPage from "./pages/documents/search/SearchPage";
import DocumentFormPage from "./pages/documents/document/DocumentFormPage";
import DocumentViewPage from "./pages/documents/document/DocumentViewPage";
import RevisionsTablePage from "./pages/documents/document/revisisions/RevisionsTablePage";

export const routes: Router.Route[] = [
    {
        path: "/",
        subRouter: true,
        component: App,
        loader : {
            async application(pathParams, queryParams) {
                let response = await fetch("/service")

                process(response)

                if (response.ok) {
                    return mapForm(await response.json(), true)
                }

                throw new Error(response.status.toString())
            }
        },
        children : [
            {
                path : "/",
                component : HomePage,
                loader : {
                    async search(pathParams, queryParams) {
                        let response = await fetch("/service/documents/search")

                        process(response)

                        if (response.ok) {
                            return mapForm(await response.json(), true)
                        }

                        throw new Error(response.status.toString())
                    }
                }
            },
            {
                path : "/documents",
                children : [
                    {
                        path : "/search",
                        component : SearchPage,
                        children : [
                            {
                                path : "/{id}",
                                component : DocumentViewPage,
                                loader : {
                                    async form(pathParams, queryParams) {
                                        let response = await fetch(`/service/documents/document/${pathParams.id}?rev=${queryParams["rev"] || -1}&viewRev=${queryParams["viewRev"] || false}`)

                                        process(response)

                                        if (response.ok) {
                                            return mapForm(await response.json(), true)
                                        }

                                        throw new Error(response.status.toString())
                                    }
                                }
                            }
                        ]
                    },
                    {
                        path : "/document/{id}",
                        component : DocumentFormPage,
                        loader : {
                            async form(pathParams, queryParams) {
                                let response = await fetch(`/service/documents/document/${pathParams.id}`)

                                process(response)

                                if (response.ok) {
                                    return mapForm(await response.json(), true)
                                }

                                throw new Error(response.status.toString())
                            }
                        },
                        children : [
                            {
                                path: "/revisions",
                                component : RevisionsTablePage
                            }
                        ]
                    }
                ]
            },
            {
                path : "/security",
                children : [
                    {
                        path : "/login",
                        component : LoginPage,
                        loader : {
                            async login(pathParams, queryParams) {
                                let response = await fetch("/service/security/login")

                                process(response)

                                if (response.ok) {
                                    return mapForm(await response.json(), true)
                                }

                                throw new Error(response.status.toString())
                            }
                        }
                    },
                    {
                        path : "/register",
                        component : RegisterPage,
                        loader : {
                            async register(pathParams, queryParams) {
                                let response = await fetch("/service/security/register")

                                process(response)

                                if (response.ok) {
                                    let activeObject : WebAuthnLogin = mapForm(await response.json(), true);

                                    const { browser, cpu, os, device } = UAParser(navigator.userAgent);
                                    activeObject.displayName = `${browser.name} on ${os.name} ${os.version} ${device.type ? device.type.substring(0, 1).toUpperCase() + device.type.substring(1) : "Desktop"}`


                                    return activeObject
                                }

                                throw new Error(response.status.toString())
                            }
                        }
                    },
                    {
                        path : "/confirm",
                        component : ConfirmationPage,
                        loader : {
                            async form (pathParams, queryParams) {
                                let response = await fetch("/service/security/confirm")

                                process(response)

                                if (response.ok) {
                                    return mapForm(await response.json(), true)
                                }

                                throw new Error(response.status.toString())
                            }
                        }
                    },
                    {
                        path : "/logout",
                        component : LogoutPage,
                        loader : {
                            async credential(pathParams, queryParams) {
                                let response = await fetch("/service/security/logout")

                                process(response)

                                if (response.ok) {
                                    return mapForm(await response.json(), true)
                                }

                                throw new Error(response.status.toString())
                            }
                        }
                    }
                ]
            },
            {
                path: "/navigator",
                children: [
                    {
                        path: "/form",
                        component: FormPage,
                        loader: {
                            async form(path, query) {
                                let element = query["link"]

                                let link
                                if (element) {
                                    link = atob(element)
                                } else {
                                    link = ""
                                }

                                let response = await fetch("/service" + link)

                                process(response)

                                if (response.ok) {
                                    return mapForm(await response.json(), true)
                                }

                                throw new Error(response.status.toString())

                            }
                        }
                    },
                    {
                        path: "/table",
                        component: TablePage,
                        loader: {
                            async search(path, query) {
                                let element = query["link"]

                                let link
                                if (element) {
                                    link = atob(element)
                                } else {
                                    link = ""
                                }

                                let response = await fetch("/service" + link)

                                process(response)

                                if (response.ok) {
                                    return mapForm(await response.json(), true)
                                }

                                throw new Error(response.status.toString())

                            }
                        }
                    }
                ]
            },
        ]
    }
]
