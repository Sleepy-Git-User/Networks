dataPoints = 60;

[oldSenderData] = ReadDataFromFile(".\measurements\old\ds1.txt", dataPoints, 2);
[oldReceiverData] = ReadDataFromFile(".\measurements\old\ds1r.txt", dataPoints, 2);

xAxis = 1:60;


senderHeader = oldSenderData(:,1);
senderTime = oldSenderData(:,2);


receiverHeader = oldReceiverData(:,1);
receiverTime = oldReceiverData(:,2);

timediff = zeros(1, dataPoints);

for i=1:dataPoints
    if receiverHeader(i) ~= senderHeader(i)

    else
        timediff(i) = receiverTime(i) - senderTime(i);
    end
end

subplot(2, 1, 1) %row, column, position
plot(xAxis, timediff) % Plot all four lines in one call to plot
grid on
title('Graph to show DatagramSocket 1')
xlabel('Block')
ylabel('Delay')
legend('Before Changes')
