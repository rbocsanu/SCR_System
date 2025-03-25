import user from '../assets/user.png'
import operatorUser from "../assets/operatoruser.png";

/*
export class ClientUser extends React.Component<any, any> {
    render() {
        return (
            <div className={'clientUser'}>
                <img src={user} alt={''}/>
                <div className='query'> </div>
            </div>
        )
    };
}

 */

function ClientUser(props) {
    return (
        <div className={'clientUser'}>
            <img src={user} alt={''}/>
            <div className='query' data-querytype={props.queryType}>{props.text}</div>
        </div>
    )
}

export default ClientUser