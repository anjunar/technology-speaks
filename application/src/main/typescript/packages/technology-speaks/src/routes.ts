import {mapForm, mapTable, Router} from "react-ui-simplicity";
import Root from "./pages/Root";
import FormPage from "./pages/navigator/FormPage";
import TablePage from "./pages/navigator/TablePage";
import HomePage from "./pages/home/HomePage";
import LoginPage from "./pages/security/LoginPage";
import RegisterPage from "./pages/security/RegisterPage";
import ConfirmationPage from "./pages/security/ConfirmationPage";
import {UAParser} from "ua-parser-js";
import Login from "./domain/security/Login";
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
            async application(language, cookie, path, pathParams, queryParams) {
                let response = await fetch("http://localhost:3000/service", {
                    headers : {cookie : `JSESSIONID=${cookie["JSESSIONID"]}`, "accept-language" : language}
                })

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
                    async search(language, cookie, path, pathParams, queryParams) {
                        let response = await fetch("http://localhost:3000/service/documents/search", {
                            headers : {cookie : `JSESSIONID=${cookie["JSESSIONID"]}`, "accept-language" : language}
                        })

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
                            async table(language, cookie, path, pathParams : PathParams, queryParams : QueryParams) {
                                const urlBuilder = new URL("/service/documents", "http://localhost:3000")
                                const searchParams = urlBuilder.searchParams;

                                searchParams.set("index", queryParams["index"] as string || "0")
                                searchParams.set("limit", queryParams["limit"] as string || "0")

                                if (queryParams.text) {
                                    searchParams.set("text", queryParams.text as string)
                                    searchParams.set("sort", "score:asc")
                                }

                                let response = await fetch(urlBuilder.toString(), {
                                    headers : {cookie : `JSESSIONID=${cookie["JSESSIONID"]}`, "accept-language" : language}
                                })

                                process(response, path)

                                if (response.ok) {
                                    return mapTable(await response.json(), true)
                                }

                                throw new Error(response.status.toString())
                            },
                            async search(language, cookie, path, pathParams, queryParams) {
                                let response = await fetch(`http://localhost:3000/service/documents/search`, {
                                    headers : {cookie : `JSESSIONID=${cookie["JSESSIONID"]}`, "accept-language" : language}
                                })

                                process(response, path)

                                if (response.ok) {
                                    let form = mapForm<DocumentSearch>(await response.json(), true);
                                    form.text = decodeURIComponent(queryParams["text"] as string || "")
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
                            async form(language, cookie, path, pathParams, queryParams) {
                                let response = await fetch(`http://localhost:3000/service/documents/document`, {
                                    headers : {cookie : `JSESSIONID=${cookie["JSESSIONID"]}`, "accept-language" : language}
                                })

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
                            async form(language, cookie, path, pathParams, queryParams) {
                                let response = await fetch(`http://localhost:3000/service/documents/document/${pathParams.id}?edit=${queryParams["edit"]}`, {
                                    headers : {cookie : `JSESSIONID=${cookie["JSESSIONID"]}`, "accept-language" : language}
                                })

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
                                            async form(language, cookie, path, pathParams, queryParams) {
                                                let response = await fetch(`http://localhost:3000/service/documents/document/${pathParams.id}/revisions/revision/${pathParams.rev}/view`, {
                                                    headers : {cookie : `JSESSIONID=${cookie["JSESSIONID"]}`, "accept-language" : language}
                                                })

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
                                            async form(language, cookie, path, pathParams, queryParams) {
                                                let response = await fetch(`http://localhost:3000/service/documents/document/${pathParams.id}/revisions/revision/${pathParams.rev}/compare`, {
                                                    headers : {cookie : `JSESSIONID=${cookie["JSESSIONID"]}`, "accept-language" : language}
                                                })

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
                            async login(language, cookie, path, pathParams, queryParams) {
                                let response = await fetch("http://localhost:3000/service/security/login", {
                                    headers : {cookie : `JSESSIONID=${cookie["JSESSIONID"]}`, "accept-language" : language}
                                })

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
                            async register(language, cookie, path, pathParams, queryParams) {
                                let response = await fetch("http://localhost:3000/service/security/register", {
                                    headers : {cookie : `JSESSIONID=${cookie["JSESSIONID"]}`, "accept-language" : language}
                                })

                                process(response, path)

                                if (response.ok) {
                                    let activeObject: Login = mapForm(await response.json(), true);

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
                            async form(language, cookie, path, pathParams, queryParams) {
                                let response = await fetch("http://localhost:3000/service/security/confirm", {
                                    headers : {cookie : `JSESSIONID=${cookie["JSESSIONID"]}`, "accept-language" : language}
                                })

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
                            async credential(language, cookie, path, pathParams, queryParams) {
                                let response = await fetch("http://localhost:3000/service/security/logout", {
                                    headers : {cookie : `JSESSIONID=${cookie["JSESSIONID"]}`, "accept-language" : language}
                                })

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
                                    async table(language, cookie, path, pathParams : PathParams, queryParams : QueryParams) {
                                        let response = await fetch(`http://localhost:3000/service/shared/i18ns?index=${queryParams["index"] || 0}&limit=10`, {
                                            headers : {cookie : `JSESSIONID=${cookie["JSESSIONID"]}`, "accept-language" : language}
                                        })

                                        process(response, path)

                                        if (response.ok) {
                                            return mapTable(await response.json(), true)
                                        }

                                        throw new Error(response.status.toString())
                                    },
                                    async search(language, cookie, path, pathParams : PathParams, queryParams : QueryParams) {
                                        let response = await fetch(`http://localhost:3000/service/shared/i18ns/search`, {
                                            headers : {cookie : `JSESSIONID=${cookie["JSESSIONID"]}`, "accept-language" : language}
                                        })

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
                                    async form(language, cookie, path, pathParams : PathParams, queryParams : QueryParams) {
                                        let response = await fetch(`http://localhost:3000/service/shared/i18ns/i18n/${pathParams["id"]}`, {
                                            headers : {cookie : `JSESSIONID=${cookie["JSESSIONID"]}`, "accept-language" : language}
                                        })

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
                            async form(language, cookie, path, query) {
                                let element = query["link"]

                                let link
                                if (element) {
                                    link = atob(element as string)
                                } else {
                                    link = ""
                                }

                                let response = await fetch("http://localhost:3000/service" + link, {
                                    headers : {cookie : `JSESSIONID=${cookie["JSESSIONID"]}`, "accept-language" : language}
                                })

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
                            async search(language, cookie, path, query) {
                                let element = query["link"]

                                let link
                                if (element) {
                                    link = atob(element as string)
                                } else {
                                    link = ""
                                }

                                let response = await fetch("http://localhost:3000/service" + link, {
                                    headers : {cookie : `JSESSIONID=${cookie["JSESSIONID"]}`, "accept-language" : language}
                                })

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
