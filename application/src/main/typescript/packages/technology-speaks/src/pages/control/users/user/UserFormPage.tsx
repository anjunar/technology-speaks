import "./UserFormPage.css"
import React from 'react';
import User from "../../../../domain/control/User";
import {SchemaForm, SchemaImage, SchemaInput, SchemaSubForm, useForm} from "react-ui-simplicity";
import SecuredProperty from "../../../../components/security/SecuredProperty";

export function UserFormPage(properties: UserFormPage.Attributes) {

    const {form} = properties

    let domain = useForm(form)

    return (
        <div className={"user-form-page center-horizontal"}>
            <div className={"panel"}>
                <SchemaForm value={domain} onSubmit={null} style={{flex : 1}}>
                    <SchemaInput name={"nickName"}/>
                    <div style={{display : "flex"}}>
                        <SchemaSubForm name={"info"} style={{flex : 1}}>
                            <SchemaInput name={"firstName"}/>
                            <SchemaInput name={"lastName"}/>
                            <SchemaInput name={"birthDate"}/>
                            <SchemaImage name={"image"} style={{width : "100px", height : "100px"}}/>
                        </SchemaSubForm>
                        <SecuredProperty descriptor={form.$descriptors.properties["info"]}/>
                    </div>
                    <SchemaSubForm name={"address"}>
                        <SchemaInput name={"street"}/>
                        <SchemaInput name={"number"}/>
                        <SchemaInput name={"zipCode"}/>
                        <SchemaInput name={"country"}/>
                    </SchemaSubForm>
                </SchemaForm>
            </div>
        </div>
    )
}

export namespace UserFormPage {
    export interface Attributes {
        form : User
    }
}

export default UserFormPage