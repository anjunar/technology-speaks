import React from "react"
import * as webauthnJson from "@github/webauthn-json";
import {
    Button,
    JsFlag,
    JSONSerializer,
    Router,
    SchemaForm,
    SchemaInput,
    useForm
} from "react-ui-simplicity";
import navigate = Router.navigate;
import Login from "../../domain/security/Login";

function LoginPage(properties: LoginPage.Attributes) {

    const {login} = properties

    const domain = useForm(login)

    async function loginAction() {

        let value = JSONSerializer(domain)

        const credentialGetOptions = await fetch('/service/security/options', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(value),
        }).then(resp => resp.json());

        const publicKeyCredential = await webauthnJson.get(credentialGetOptions);

        const responseFinish = await fetch('/service/security/finish', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(publicKeyCredential),
        });

        if (responseFinish.ok) {
            const params = new URLSearchParams(window.location.search)
            navigate(params.get("redirect"), true)
        } else {
            alert("Something went wrong")
        }

    }

    return (
        <div className={"login-page"} style={{display : "flex", justifyContent : "center", alignItems : "center", height : "100%"}}>
            <div>
                <h1>Login</h1>
                <SchemaForm actionRel={"fallback"} value={domain} onSubmit={loginAction} style={{width : "300px"}}>
                    <SchemaInput name={"username"}/>
                    <JsFlag showWhenJs={false}>
                        <SchemaInput name={"password"}/>
                    </JsFlag>
                    <div style={{display : "flex", justifyContent : "flex-end"}}>
                        <JsFlag showWhenJs={true}>
                            <Button name={"login"}>WebAuthn</Button>
                        </JsFlag>
                        <Button name={"fallback"}>Login</Button>
                    </div>
                </SchemaForm>
            </div>
        </div>
    )
}

namespace LoginPage {
    export interface Attributes {
        login : Login
    }
}

export default LoginPage