<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Participants List</title>
    <link rel="stylesheet" href="{{ asset('css/bootstrap.min.css') }}">
</head>
<body>
<div class="container mt-5">
    <h1>Participants from {{ $school->Name }}</h1>
    <ul class="list-group">
        @foreach ($participants as $participant)
            <li class="list-group-item">
                <a href="{{ route('participants.show', $participant->PupilID) }}">
                    {{ $participant->FirstName }} {{ $participant->LastName }}
                </a>
            </li>
        @endforeach
    </ul>
    <a href="{{ route('school.index') }}" class="btn btn-primary mt-3">Back to Schools List</a>
</div>
</body>
</html>