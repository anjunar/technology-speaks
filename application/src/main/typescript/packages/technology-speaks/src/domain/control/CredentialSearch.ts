import {AbstractSearch, Basic, Entity} from "react-ui-simplicity";
import Role from "./Role";

@Entity("CredentialTableSearch")
export default class CredentialSearch extends AbstractSearch {

    $type = "CredentialTableSearch"

    @Basic()
    displayName : string

    @Basic()
    roles : Role[]

}