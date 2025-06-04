import {Button, TextField} from "@vaadin/react-components";
import {useState} from "react";
import {ChatbotService} from "Frontend/generated/endpoints";
import Markdown from "react-markdown";


export default function Chat() {
    const [question, setQuestion] = useState<string>("");
    const [response, setResponse] = useState<string>("");

    async function send() {
        console.log('send..')
        console.log(question)
        ChatbotService.findAnswer(question).then(resp => {
            setResponse(resp!);
        });
    }

    return (
        <div className="p-m">
            <h1>Chat Bot</h1>
            <div>
                <TextField style={{width:'80%'}}
                onChange={(e)=>setQuestion(e.target.value)}
                ></TextField>
                <Button theme="primary" onClick={send}>Send</Button>

                <div>
                    <Markdown>
                        {response}
                    </Markdown>
                </div>
            </div>
        </div>
    );
}
