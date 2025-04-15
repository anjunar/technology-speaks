import {ActiveObject, Basic, Entity} from "react-ui-simplicity";

@Entity("WebAuthnLogin")
export default class WebAuthnLogin extends ActiveObject {

    $type = "WebAuthnLogin"

    @Basic()
    username : string

    @Basic()
    displayName : string

}