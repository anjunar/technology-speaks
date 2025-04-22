import highlight from 'highlight.js';
import typescript from 'highlight.js/lib/languages/typescript';
import xml from 'highlight.js/lib/languages/xml';
import React, {useMemo} from "react";

highlight.registerLanguage('typescript', typescript);
highlight.registerLanguage('xml', xml);

function Highlight(properties : Highlight.Attributes) {

    const {language, children} = properties

    const html = useMemo(() => {
        return highlight.highlight(children, {language : language})
    }, []);

    return (
        <div className={"highlight"} style={{overflowX : "auto", overflowY : "hidden"}}>
            <pre dangerouslySetInnerHTML={{__html: html.value}}></pre>
        </div>

    )
}

namespace Highlight {
    export interface Attributes {
        language : string
        children : string
    }
}

export default Highlight