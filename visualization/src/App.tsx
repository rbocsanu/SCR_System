import {useEffect, useState} from 'react'
import {Client} from "@stomp/stompjs";
import './App.css'
import ClientUser from "./components/ClientUser.tsx";
import OperatorUser from "./components/OperatorUser.tsx";
import ServerUser from "./components/ServerUser.tsx";
import broker from "./assets/broker.png";

class UserData {
    id: string;
    query: QueryData

    public constructor(id: string, query) {
        this.id = id;
        this.query = query
    }
}

class OperatorUserData {
    id: string;
    public constructor(id: string) {
        this.id = id;
    }
}

class ServerUserData {
    id: string;
    queryText: string;
    hasActiveQuery: boolean;
    queryType: string;

    public constructor(name: string, queryText: string, hasActiveQuery: boolean, queryType: string) {
        this.id = name;
        this.queryText = queryText;
        this.hasActiveQuery = hasActiveQuery;
        this.queryType = queryType;
    }
}

class BrokerQueryData {
    text: string;

    public constructor(text) {
        this.text = text;
    }

}

class QueryData {
    queryText: string;
    queryType: string;

    public constructor(text: string, queryType: string) {
        this.queryText = text;
        this.queryType = queryType;
    }
}

function clientAdded(id: string, clients: [UserData] , setClients) {
    const newClient = new UserData(name, new QueryData('', ''));
    console.log(clients);
    setClients([...clients, newClient]);
}

function clientQueried(id: string, queryText: string, clients: [UserData] , setClients) {
    const newClient = new UserData(id, queryText);
    setClients(clients.map(client => client.id === id ? newClient : client));
}

function clientRemoved() {

}

function operatorAdded(id: string, operators: [UserData], setOperators) {
    const newOperator = new UserData(id, new QueryData('', ''));
    setOperators([...operators, newOperator])
}

function querySelected(query: QueryData, brokerQueries: [QueryData], setBrokerQueries) {
    setBrokerQueries([...brokerQueries, query])
}


function App() {

    const [clients, setClients] = useState([]);
    const [operators, setOperators] = useState([]);
    const [servers, setServers] = useState([]);
    const [brokerQueries, setBrokerQueries] = useState([]);

    function InitConnection() {

        const maxConnectAttempts = 1;
        let currentTry = 0;

        const stompClient = new Client({
            brokerURL: 'ws://localhost:8080/broker',
            connectHeaders: {
                username: "visualization",
                password: "visualizationpass"
            },
            onConnect: () => {
                console.log('Connected');
                stompClient.subscribe('/user/queue/reply', (msg) => {
                    const body = JSON.parse(msg.body)
                    console.log(body.requestType)
                    switch (body.requestType) {
                        case 'CLIENT_CONNECTED':
                            console.log("Client connected: " + body.event)

                            clientAdded(body.event, clients, setClients)
                            break;
                    }
                    //showGreeting(JSON.parse(greeting.body).content);
                });
            },
            beforeConnect: () => {
                currentTry++

                if (currentTry > maxConnectAttempts) {
                    console.log(`Exceeds max attempts (${maxConnectAttempts}), will not try to connect now`);
                    stompClient.deactivate();
                }
            }
        });
        stompClient.activate();

    }

    useEffect(() => {
        console.log("Using effect")
        InitConnection();
        //clientAdded("Bob", clients, setClients);

        setClients([new UserData("Bob", new QueryData('Glass of water...', 'task')),
            new UserData("Jim", new QueryData('Small chat...', 'service')),
            new UserData("Jim2", new QueryData('Play chess', 'service'))])
        setOperators([new UserData('1', new QueryData('Heyo','task')),
            new UserData('2', new QueryData('Heyo as well','service'))])
        setServers([new UserData('1111', new QueryData('Processing', 'task')),
            new UserData('1121',new QueryData('Processing as well...', 'service'))]);
        setBrokerQueries([new QueryData('Getting a cane', 'task'),
            new QueryData('Getting a bottle of water', 'service'),
            //new QueryData('Getting a bottle of water', 'service'),
            //new QueryData('Getting a bottle of water', 'service'),
            //new QueryData('Getting a bottle of water', 'service'),
            //new QueryData('Getting a bottle of water', 'service'),
            new QueryData('Getting a bottle of water', 'service')])
        /*

        setClients([new ClientUserData("Bob", ''), new ClientUserData("Bob1", 'Heyo'), new ClientUserData("Bob2",
            'chahcha')])//new ClientUserData("Bob2"),new ClientUserData("Bob3"),new ClientUserData("Bob4"), new ClientUserData("Jim")])
        //setOperators([new OperatorUserData('1')])
        //setOperators([new OperatorUserData('1'), new OperatorUserData('2')])
        setOperators([new OperatorUserData('1'), new OperatorUserData('2'), new OperatorUserData('3')])

         */
    }, [])

    return (

        <div className="container">
            <div className="user">

                <div className={'holderClients'}>
                    {clients.map((client) => (
                        <ClientUser key={client.name} text={client.query.queryText} queryType={client.query.queryType}/>
                    ))}
                </div>

            </div>
            <div className="broker">
                <div className='parentHolderBroker'>
                    <div className='holderBroker'>
                        {brokerQueries.map((query) => (
                            <div className='query' data-querytype={query.queryType}>{query.queryText}</div>
                        ))}
                    </div>
                    <img src={broker} alt='' />
                </div>
            </div>
            <div className="operator">

                <div className={'holderOperators'}>
                    {operators.map((operator) =>
                        <OperatorUser key={operator.id} queryText={operator.query.queryText} queryType={operator.query.queryType} />
                    )}
                </div>

            </div>
            <div className="server">

                <div className={'holderServer'}>
                    {servers.map((server) =>
                    <ServerUser key={server.id} queryText={server.query.queryText} queryType={server.query.queryType} />)}
                </div>

            </div>
        </div>

        /*
        <>
            <div className="query-box">
                <text>Hello World! You are fun</text>
            </div>
            <div className="query-box">
                <text>Hello World! You are fun</text>
            </div>
        </>

         */
    )

    /*
    <>
    <div>
      <a href="https://vite.dev" target="_blank">
        <img src={viteLogo} className="logo" alt="Vite logo" />
      </a>
      <a href="https://react.dev" target="_blank">
        <img src={reactLogo} className="logo react" alt="React logo" />
      </a>
    </div>
    <h1>Vite + React</h1>
    <div className="card">
      <button onClick={() => setCount((count) => count + 1)}>
        count is {count}
      </button>
      <p>
        Edit <code>src/App.tsx</code> and save to test HMR
      </p>
    </div>
    <p className="read-the-docs">
      Click on the Vite and React logos to learn more
    </p>
    </>
    */
    }

export default App
