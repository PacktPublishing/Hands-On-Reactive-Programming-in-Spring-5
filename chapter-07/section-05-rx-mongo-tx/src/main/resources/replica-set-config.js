rs.initiate({
    _id: 'reactive',
    members: [
        {_id : 1, host : 'host.docker.internal:27017'},
        {_id : 2, host : 'host.docker.internal:27018'},
        {_id : 3, host : 'host.docker.internal:27019'}
    ]
});