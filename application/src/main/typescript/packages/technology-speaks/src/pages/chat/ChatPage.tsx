import './ChatPage.css'
import React, {useLayoutEffect, useRef, useState} from 'react';

export function ChatPage(properties: ChatPage.Attributes) {

    const {} = properties

    const [buffer, setBuffer] = useState("")

    const scrollRef = useRef<HTMLDivElement>(null);

    function onKeyUp(event : React.KeyboardEvent<HTMLInputElement>) {
        if (event.key === "Enter") {
            event.preventDefault()

            let eventSource = new EventSource(`/service/chat?text=${encodeURIComponent(event.currentTarget.value)}`);

            eventSource.onmessage = (e) => {

                if (e.data === "!Done!") {
                    eventSource.close()
                } else {
                    setBuffer((prev) => {
                        const next = prev + JSON.parse(e.data).text

                        requestAnimationFrame(() => {
                            if (scrollRef.current) {
                                scrollRef.current.scrollTop = scrollRef.current.scrollHeight;
                            }
                        });

                        return next;
                    });
                }
            }
        }
    }

    return (
        <div className={"chat-page"}>
            <div className={"center-horizontal"}>
                <div ref={scrollRef} style={{minWidth : "360px", maxWidth: "800px", width : "100%", height : "100%", overflowY : "auto", padding : "20px"}}>
                    <div>
                        <h1>Chat</h1>
                    </div>
                    <div dangerouslySetInnerHTML={{__html : buffer}}></div>
                    <div style={{backgroundColor : "var(--color-background-tertiary)"}}>
                        <input type={"text"} placeholder={"Message"} onKeyUp={onKeyUp}/>
                    </div>
                </div>
            </div>
        </div>
    )
}

namespace ChatPage {
    export interface Attributes {
    }
}

export default ChatPage;