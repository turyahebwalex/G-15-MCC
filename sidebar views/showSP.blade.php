
<!-- resources/views/schools/index.blade.php -->
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Schools List</title>
    <link rel="stylesheet" href="{{ asset('css/bootstrap.min.css') }}">
</head>
<body>
<div class="container mt-5">
    <h1>SCHOOLS' PERFOMANCE OVER THE YEARS AND TIME</h1>
    <ul class="list-group">
        @foreach($schools as $school)
            <li class="list-group-item">
                <a href="{{ route('schools.show', $school->id) }}">{{ $school->Name }}</a>
            </li>
        @endforeach
    </ul>
</div>
</body>
</html>
