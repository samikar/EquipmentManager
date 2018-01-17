$(function() {
  $('form').submit(function(){
    $.post('/rest/take', function() {
      window.location = '/index.html';
    });
    return false;
  });
});
