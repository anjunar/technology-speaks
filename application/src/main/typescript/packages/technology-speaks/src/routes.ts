import {mapForm, mapTable, Router} from "react-ui-simplicity";
import Root from "./pages/Root";
import FormPage from "./pages/navigator/FormPage";
import TablePage from "./pages/navigator/TablePage";
import HomePage from "./pages/home/HomePage";
import LoginPage from "./pages/security/LoginPage";
import RegisterPage from "./pages/security/RegisterPage";
import ConfirmationPage from "./pages/security/ConfirmationPage";
import {UAParser} from "ua-parser-js";
import WebAuthnLogin from "./domain/security/WebAuthnLogin";
import LogoutPage from "./pages/security/LogoutPage";
import DocumentSearchPage from "./pages/documents/search/DocumentSearchPage";
import DocumentFormPage from "./pages/documents/document/DocumentFormPage";
import DocumentViewPage from "./pages/documents/document/DocumentViewPage";
import RevisionsTablePage from "./pages/documents/document/revisisions/RevisionsTablePage";
import DocumentSearch from "./domain/document/DocumentSearch";
import I18nTablePage from "./pages/shared/i18n/I18nTablePage";
import I18nFormPage from "./pages/shared/i18n/I18nFormPage";
import QueryParams = Router.QueryParams;
import PathParams = Router.PathParams;

export function process(response: Response, redirect : string) {
    if (response.status === 403) {
        throw new Router.RedirectError(`/security/login?redirect=${redirect}`)
    }
}

export const routes: Router.Route[] = [
    {
        path: "/",
        subRouter: true,
        component: Root,
        loader: {
            async application(path, pathParams, queryParams) {
                let response = await fetch("http://localhost:3000/service")

                process(response, path)

                if (response.ok) {
                    return mapForm(await response.json(), false)
                }

                throw new Error(response.status.toString())
            }
        },
        children: [
            {
                path: "/",
                component: HomePage,
                loader: {
                    async search(path, pathParams, queryParams) {
                        let response = await fetch("http://localhost:3000/service/documents/search")

                        process(response, path)

                        if (response.ok) {
                            return mapForm(await response.json(), true)
                        }

                        throw new Error(response.status.toString())
                    }
                }
            },
            {
                path: "/documents",
                children: [
                    {
                        path: "/search",
                        component: DocumentSearchPage,
                        loader: {
                            async table(path, pathParams : PathParams, queryParams : QueryParams) {
                                const urlBuilder = new URL("/service/documents", "http://localhost:3000")
                                const searchParams = urlBuilder.searchParams;

                                searchParams.set("index", queryParams["index"] as string || "0")
                                searchParams.set("limit", "5")

                                if (queryParams.text) {
                                    searchParams.set("text", queryParams.text as string)
                                    searchParams.set("sort", "score:asc")
                                }

                                let response = await fetch(urlBuilder.toString())

                                process(response, path)

                                if (response.ok) {
                                    return mapTable(await response.json(), true)
                                }

                                throw new Error(response.status.toString())
                            },
                            async search(path, pathParams, queryParams) {
                                let response = await fetch(`http://localhost:3000/service/documents/search`)

                                process(response, path)

                                if (response.ok) {
                                    let form = mapForm<DocumentSearch>(await response.json(), true);
                                    form.text = decodeURIComponent(queryParams["text"] as string)
                                    return form
                                }

                                throw new Error(response.status.toString())
                            }
                        }
                    },
                    {
                        path: "/document",
                        component : DocumentFormPage,
                        loader: {
                            async form(path, pathParams, queryParams) {
                                let response = await fetch(`http://localhost:3000/service/documents/document`)

                                process(response, path)

                                if (response.ok) {
                                    return mapForm(await response.json(), true)
                                }

                                throw new Error(response.status.toString())
                            }
                        }
                    },
                    {
                        path: "/document/:id",
                        dynamic: (path, query) => {
                            if (query["edit"] === "true") {
                                return DocumentFormPage
                            } else {
                                return DocumentViewPage
                            }
                        },
                        loader: {
                            async form(path, pathParams, queryParams) {
                                let response = await fetch(`http://localhost:3000/service/documents/document/${pathParams.id}?edit=${queryParams["edit"]}`)

                                process(response, path)

                                if (response.ok) {
                                    return mapForm(await response.json(), true)
                                }

                                throw new Error(response.status.toString())
                            }
                        },
                        children: [
                            {
                                path: "/revisions",
                                children: [
                                    {
                                        path: "/search",
                                        component: RevisionsTablePage
                                    },
                                    {
                                        path: "/revision/:rev/view",
                                        component: DocumentViewPage,
                                        loader: {
                                            async form(path, pathParams, queryParams) {
                                                let response = await fetch(`http://localhost:3000/service/documents/document/${pathParams.id}/revisions/revision/${pathParams.rev}/view`)

                                                process(response, path)

                                                if (response.ok) {
                                                    return mapForm(await response.json(), true)
                                                }

                                                throw new Error(response.status.toString())
                                            }
                                        }
                                    }, {
                                        path: "/revision/:rev/compare",
                                        component: DocumentViewPage,
                                        loader: {
                                            async form(path, pathParams, queryParams) {
                                                let response = await fetch(`http://localhost:3000/service/documents/document/${pathParams.id}/revisions/revision/${pathParams.rev}/compare`)

                                                process(response, path)

                                                if (response.ok) {
                                                    return mapForm(await response.json(), true)
                                                }

                                                throw new Error(response.status.toString())
                                            }
                                        }
                                    }]
                            }
                        ]
                    }
                ]
            },
            {
                path: "/security",
                children: [
                    {
                        path: "/login",
                        component: LoginPage,
                        loader: {
                            async login(path, pathParams, queryParams) {
                                let response = await fetch("http://localhost:3000/service/security/login")

                                process(response, path)

                                if (response.ok) {
                                    return mapForm(await response.json(), true)
                                }

                                throw new Error(response.status.toString())
                            }
                        }
                    },
                    {
                        path: "/register",
                        component: RegisterPage,
                        loader: {
                            async register(path, pathParams, queryParams) {
                                let response = await fetch("http://localhost:3000/service/security/register")

                                process(response, path)

                                if (response.ok) {
                                    let activeObject: WebAuthnLogin = mapForm(await response.json(), true);

                                    const {browser, cpu, os, device} = UAParser(navigator.userAgent);
                                    activeObject.displayName = `${browser.name} on ${os.name} ${os.version} ${device.type ? device.type.substring(0, 1).toUpperCase() + device.type.substring(1) : "Desktop"}`


                                    return activeObject
                                }

                                throw new Error(response.status.toString())
                            }
                        }
                    },
                    {
                        path: "/confirm",
                        component: ConfirmationPage,
                        loader: {
                            async form(path, pathParams, queryParams) {
                                let response = await fetch("http://localhost:3000/service/security/confirm")

                                process(response, path)

                                if (response.ok) {
                                    return mapForm(await response.json(), true)
                                }

                                throw new Error(response.status.toString())
                            }
                        }
                    },
                    {
                        path: "/logout",
                        component: LogoutPage,
                        loader: {
                            async credential(path, pathParams, queryParams) {
                                let response = await fetch("http://localhost:3000/service/security/logout")

                                process(response, path)

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
                path : "/shared",
                children : [
                    {
                        path: "/i18ns",
                        children: [
                            {
                                path : "/search",
                                component : I18nTablePage,
                                loader : {
                                    async table(path, pathParams : PathParams, queryParams : QueryParams) {
                                        let response = await fetch(`http://localhost:3000/service/shared/i18ns?index=${queryParams["index"] || 0}&limit=10`)

                                        process(response, path)

                                        if (response.ok) {
                                            return mapTable(await response.json(), true)
                                        }

                                        throw new Error(response.status.toString())
                                    },
                                    async search(path, pathParams : PathParams, queryParams : QueryParams) {
                                        let response = await fetch(`http://localhost:3000/service/shared/i18ns/search`)

                                        process(response, path)

                                        if (response.ok) {
                                            return mapForm(await response.json(), true)
                                        }

                                        throw new Error(response.status.toString())
                                    }
                                }
                            },
                            {
                                path : "/i18n/:id",
                                component : I18nFormPage,
                                loader : {
                                    async form(path, pathParams : PathParams, queryParams : QueryParams) {
                                        let response = await fetch(`http://localhost:3000/service/shared/i18ns/i18n/${pathParams["id"]}`)

                                        process(response, path)

                                        if (response.ok) {
                                            return mapForm(await response.json(), true)
                                        }

                                        throw new Error(response.status.toString())
                                    }
                                }
                            }
                        ]

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
                                    link = atob(element as string)
                                } else {
                                    link = ""
                                }

                                let response = await fetch("http://localhost:3000/service" + link)

                                process(response, path)

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
                                    link = atob(element as string)
                                } else {
                                    link = ""
                                }

                                let response = await fetch("http://localhost:3000/service" + link)

                                process(response, path)

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
