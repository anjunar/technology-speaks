import React from 'react';
import CodeMirrorTag from "../domain/CodeMirrorTag";

export function VersionControl(properties: VersionControl.Attributes) {

    const {tags} = properties

    return (
        <div className={"version-control"}>
            {
                tags.map(tag => (
                    <div key={tag.id} className={"tag"}>{tag.name}</div>
                ))
            }
        </div>
    )
};

export namespace VersionControl {
    export interface Attributes {
        tags : CodeMirrorTag[]

    }
}

export default VersionControl;